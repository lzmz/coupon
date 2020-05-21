package io.github.lzmz.coupon.controller;

import io.github.lzmz.coupon.dto.request.CouponCalculateDto;
import io.github.lzmz.coupon.dto.response.CouponSolutionDto;
import io.github.lzmz.coupon.endpoint.CouponEndpoint;
import io.github.lzmz.coupon.exceptions.InsufficientAmountException;
import io.github.lzmz.coupon.service.CouponService;
import io.github.lzmz.coupon.service.ItemConsumerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Tag(name = "Coupon")
@RestController
@RequestMapping(value = CouponEndpoint.BASE)
public class CouponController {

    private final ItemConsumerService itemConsumerService;
    private final CouponService couponService;

    public CouponController(ItemConsumerService itemConsumerService, CouponService couponService) {
        this.itemConsumerService = itemConsumerService;
        this.couponService = couponService;
    }

    /**
     * Retrieves a subset of the given {@code items} that maximizes the total spending but does not
     * exceed the {@code amount} supplied. Additionally, it includes the total amount associated to
     * the items of the solution.
     *
     * @param couponCalculateDto the coupon calculation request body.
     * @return a list of item IDs that maximizes the total spending, and the amount associated to these
     * items.
     * @throws InsufficientAmountException if none item can be bought with the given amount.
     */
    @Operation(summary = "Retrieves a subset of the given items that maximizes the total spending but " +
            "does not exceed the amount supplied. Additionally, it includes the total amount associated " +
            "to the items of the solution.")
    @PostMapping()
    public ResponseEntity<CouponSolutionDto> calculate(@Valid @RequestBody CouponCalculateDto couponCalculateDto) throws InsufficientAmountException {
        Map<String, Float> items = itemConsumerService.getItemsPrice(couponCalculateDto.getItemsId());
        List<String> calculated = couponService.calculate(items, couponCalculateDto.getAmount());
        float total = Float.parseFloat(calculated.remove(calculated.size() - 1));
        return new ResponseEntity<>(new CouponSolutionDto(calculated, total), HttpStatus.OK);
    }
}