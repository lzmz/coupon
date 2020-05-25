package io.github.lzmz.coupon.service;

import io.github.lzmz.coupon.exception.InsufficientAmountException;

import java.util.List;
import java.util.Map;

public interface CouponService {

    /**
     * Retrieves a subset of the given {@code items} that maximizes the total spending but does not
     * exceed the {@code amount} supplied.
     * <p>The returned subset is sorted by the items ID.</p>
     *
     * @param items  a {@link Map} instance with ID-price as key-value.
     * @param amount the value of the coupon that will not be exceeded.
     * @return a list of item IDs that maximizes total spend without exceeding the {@code amount} provided.
     * <p>{@code null} if the given {@code Map} of items is null or empty, or the amount is null.</p>
     * @throws InsufficientAmountException if none item can be bought with the given amount.
     */
    List<String> calculate(Map<String, Float> items, Float amount) throws InsufficientAmountException;

    /**
     * Retrieves the total sum of the prices of the given items. {@code null} if any of the given IDs hasn't
     * a corresponding price in the provided {@code items}.
     *
     * @param ids   a list of item IDs from which the total price will be calculated.
     * @param items a {@link Map} instance with ID-price as key-value.
     * @return the total sum of the prices of the given items.
     * <p>{@code null} if the given {@code List} of ids or the {@code Map} of items is null.</p>
     */
    Float calculateTotalAmount(List<String> ids, Map<String, Float> items);
}
