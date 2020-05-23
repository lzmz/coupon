package io.github.lzmz.coupon;


import io.github.lzmz.coupon.exceptions.InsufficientAmountException;
import io.github.lzmz.coupon.service.CouponService;
import io.github.lzmz.coupon.service.implementation.CouponServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CouponServiceTest {

    private final CouponService couponService = new CouponServiceImpl();

    @Test
    public void calculateTotalAmount_nullIds_shouldReturnNull() {
        Float total = couponService.calculateTotalAmount(null, new HashMap<>());
        assertNull(total);
    }

    @Test
    public void calculateTotalAmount_nullItems_shouldReturnNull() {
        Float total = couponService.calculateTotalAmount(Collections.emptyList(), null);
        assertNull(total);
    }

    @Test
    public void calculateTotalAmount_nullPrice_shouldReturnNull() {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 100F);
        items.put("MLA2", 210F);
        List<String> ids = new ArrayList<>(items.keySet());
        items.remove("MLA1");

        Float total = couponService.calculateTotalAmount(ids, items);
        assertNull(total);
    }

    @Test
    public void calculateTotalAmount_validEntrySet_shouldReturnTotal() {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 100F);
        items.put("MLA2", 210F);
        items.put("MLA3", 260F);
        items.put("MLA4", 80F);
        items.put("MLA5", 90F);
        List<String> solutionIds = Arrays.asList("MLA1", "MLA2", "MLA4", "MLA5");
        Float solutionAmount = 480F;

        Float total = couponService.calculateTotalAmount(solutionIds, items);
        assertEquals(solutionAmount, total);
    }

    @Test
    public void calculate_nullItems_shouldReturnNull() throws InsufficientAmountException {
        List<String> calculated = couponService.calculate(null, 500F);
        assertNull(calculated);
    }

    @Test
    public void calculate_nullAmount_shouldReturnNull() throws InsufficientAmountException {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 200F);
        List<String> calculated = couponService.calculate(items, null);
        assertNull(calculated);
    }

    @Test
    public void calculate_emptyItemsMap_shouldReturnNull() throws InsufficientAmountException {
        List<String> calculated = couponService.calculate(new HashMap<>(), 500F);
        assertNull(calculated);
    }

    @Test
    public void calculate_insufficientAmount_shouldThrowInsufficientAmountException() {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 200F);
        assertThrows(InsufficientAmountException.class, () -> couponService.calculate(items, 100F));
    }

    @Test
    public void calculate_validEntrySet1_shouldReturnSolution() throws InsufficientAmountException {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 100F);
        Float solutionAmount = 100F;
        List<String> solutionIds = Collections.singletonList("MLA1");

        List<String> calculated = couponService.calculate(items, 500F);
        Float total = couponService.calculateTotalAmount(calculated, items);

        assertEquals(solutionAmount, total);
        assertIterableEquals(solutionIds, calculated);
    }

    @Test
    public void calculate_validEntrySet2_shouldReturnSolution() throws InsufficientAmountException {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 100F);
        items.put("MLA2", 210F);
        items.put("MLA3", 260F);
        items.put("MLA4", 80F);
        items.put("MLA5", 90F);
        Float solutionAmount = 480F;
        List<String> solutionIds = Arrays.asList("MLA1", "MLA2", "MLA4", "MLA5");

        List<String> calculated = couponService.calculate(items, 500F);
        Float total = couponService.calculateTotalAmount(calculated, items);

        assertEquals(solutionAmount, total);
        assertIterableEquals(solutionIds, calculated);
    }

    @Test
    public void calculate_validEntrySet3_shouldReturnSolution() throws InsufficientAmountException {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 270F);
        items.put("MLA2", 100F);
        items.put("MLA3", 90F);
        items.put("MLA4", 80F);
        Float solutionAmount = 460F;
        List<String> solutionIds = Arrays.asList("MLA1", "MLA2", "MLA3");

        List<String> calculated = couponService.calculate(items, 500F);
        Float total = couponService.calculateTotalAmount(calculated, items);

        assertEquals(solutionAmount, total);
        assertIterableEquals(solutionIds, calculated);
    }

    @Test
    public void calculate_validEntrySet4_shouldReturnSolution() throws InsufficientAmountException {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 10.65F);
        items.put("MLA2", 10.65F);
        Float solutionAmount = 10.65F;
        List<String> solutionIds = Collections.singletonList("MLA2");

        List<String> calculated = couponService.calculate(items, 21.29F);
        Float total = couponService.calculateTotalAmount(calculated, items);

        assertEquals(solutionAmount, total);
        assertIterableEquals(solutionIds, calculated);
    }

    @Test
    public void calculate_validEntrySet5_shouldReturnSolution() throws InsufficientAmountException {
        Map<String, Float> items = new HashMap<>();
        items.put("MLA1", 10.65F);
        items.put("MLA2", 10.65F);
        Float solutionAmount = 21.3F;
        List<String> solutionIds = Arrays.asList("MLA1", "MLA2");

        List<String> calculated = couponService.calculate(items, 21.30F);
        Float total = couponService.calculateTotalAmount(calculated, items);

        assertEquals(solutionAmount, total);
        assertIterableEquals(solutionIds, calculated);
    }
}