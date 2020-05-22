package io.github.lzmz.coupon.service;

import java.util.List;
import java.util.Map;

public interface ItemConsumerService {

    /**
     * Retrieves a {@link Map} instance with ID-price as key-value.
     *
     * @param ids the IDs of the items from which the price will be recovered.
     * @return the price of each of the given items in {@code ids}.
     */
    Map<String, Float> getItemsPrice(List<String> ids);

    /**
     * Retrieves the price of the item associated with the given {@code id}.
     *
     * @param id the ID of the item from which the price will be recovered.
     * @return the price of the item.
     */
    Float getItemPrice(String id);
}