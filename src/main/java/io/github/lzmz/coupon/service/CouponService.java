package io.github.lzmz.coupon.service;

import java.util.List;
import java.util.Map;

public interface CouponService {

    /**
     * Retrieves a subset of the given {@code items} that maximizes the total spending but does not
     * exceed the {@code amount} supplied.
     * <p>The maximized total expense is included in the last position of the list.</p>
     *
     * @param items  a {@link Map} instance with ID-price as key-value.
     * @param amount the value of the coupon that will not be exceeded.
     * @return a list of item IDs that maximizes total spend without exceeding the {@code amount} provided.
     */
    List<String> calculate(Map<String, Float> items, Float amount);
}
