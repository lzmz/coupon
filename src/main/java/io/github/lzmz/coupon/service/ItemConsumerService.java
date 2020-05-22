package io.github.lzmz.coupon.service;

import io.github.lzmz.coupon.exceptions.NoItemPriceException;

import java.util.List;
import java.util.Map;

public interface ItemConsumerService {

    /**
     * Retrieves a {@link Map} instance with ID-price as key-value.
     *
     * @param ids the IDs of the items from which the price will be recovered.
     * @return the price of each of the given items in {@code ids}.
     * @throws NoItemPriceException if one or more of the items has no price.
     */
    Map<String, Float> getItemsPrice(List<String> ids) throws NoItemPriceException;

    /**
     * Retrieves the price of the item associated with the given {@code id}.
     *
     * @param id the ID of the item from which the price will be recovered.
     * @return the price of the item.
     * @throws NoItemPriceException if the item has no associated price.
     */
    Float getItemPrice(String id) throws NoItemPriceException;
}