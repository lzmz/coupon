package io.github.lzmz.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lzmz.coupon.controller.CouponController;
import io.github.lzmz.coupon.dto.request.CouponCalculateDto;
import io.github.lzmz.coupon.endpoint.CouponEndpoint;
import io.github.lzmz.coupon.exceptions.ApiErrorCode;
import io.github.lzmz.coupon.exceptions.CustomRestExceptionHandler;
import io.github.lzmz.coupon.exceptions.InsufficientAmountException;
import io.github.lzmz.coupon.exceptions.NoItemPriceException;
import io.github.lzmz.coupon.service.CouponService;
import io.github.lzmz.coupon.service.ItemConsumerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ObjectMapper.class})
public class CouponControllerTest {

    private ItemConsumerService itemConsumerService;
    private CouponService couponService;
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        itemConsumerService = mock(ItemConsumerService.class);
        couponService = mock(CouponService.class);
        CouponController couponController = new CouponController(itemConsumerService, couponService);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(couponController)
                .setControllerAdvice(new CustomRestExceptionHandler(objectMapper))
                .build();
    }

    @Test
    public void calculate_validBody_shouldReturnOkAndSolution() throws Exception {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 100F);
        items.put("MLA2", 210F);
        List<String> ids = new ArrayList<>(items.keySet());
        Float amount = 500F;
        Float total = 310F;

        when(itemConsumerService.getItemsPrice(ids)).thenReturn(items);
        when(couponService.calculate(items, amount)).thenReturn(ids);
        when(couponService.calculateTotalAmount(ids, items)).thenReturn(total);

        CouponCalculateDto couponCalculateDto = new CouponCalculateDto(ids, amount);

        ResultActions result = mockMvc.perform(
                post(CouponEndpoint.BASE)
                        .content(objectMapper.writeValueAsString(couponCalculateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE));

        result.andExpect(status().isOk());
        result.andExpect((jsonPath("$.item_ids").value(hasSize(ids.size()))));
        result.andExpect((jsonPath("$.item_ids", contains(ids.toArray()))));
        result.andExpect((jsonPath("$.total").value(total)));
    }

    @Test
    public void calculate_validBodyNoItemPrice_shouldReturnBadRequest() throws Exception {
        List<String> ids = new ArrayList<>(Arrays.asList("MLA1", "MLA2"));

        when(itemConsumerService.getItemsPrice(ids)).thenThrow(new NoItemPriceException(ids));

        CouponCalculateDto couponCalculateDto = new CouponCalculateDto(ids, 500F);

        ResultActions result = mockMvc.perform(
                post(CouponEndpoint.BASE)
                        .content(objectMapper.writeValueAsString(couponCalculateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE));

        result.andExpect(status().isBadRequest());
        result.andExpect((jsonPath("$.code").value(ApiErrorCode.NO_ITEM_PRICE)));
    }

    @Test
    public void calculate_validBodyInsufficientAmount_shouldReturnNotFound() throws Exception {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 100F);
        items.put("MLA2", 210F);
        List<String> ids = new ArrayList<>(items.keySet());
        Float amount = 500F;
        Float total = 310F;

        when(itemConsumerService.getItemsPrice(ids)).thenReturn(items);
        when(couponService.calculate(items, amount)).thenThrow(InsufficientAmountException.class);
        when(couponService.calculateTotalAmount(ids, items)).thenReturn(total);

        CouponCalculateDto couponCalculateDto = new CouponCalculateDto(ids, amount);

        ResultActions result = mockMvc.perform(
                post(CouponEndpoint.BASE)
                        .content(objectMapper.writeValueAsString(couponCalculateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE));

        result.andExpect(status().isNotFound());
        result.andExpect((jsonPath("$.code").value(ApiErrorCode.INSUFFICIENT_AMOUNT)));
    }
}