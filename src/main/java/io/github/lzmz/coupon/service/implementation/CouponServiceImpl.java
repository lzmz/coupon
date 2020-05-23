package io.github.lzmz.coupon.service.implementation;

import io.github.lzmz.coupon.exceptions.InsufficientAmountException;
import io.github.lzmz.coupon.service.CouponService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {

    /**
     * The value by which prices will be multiplied to consider pennies
     * (e.g. if the value is 100, two pennies will be considered).
     */
    final static float DECIMALS = 100F;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> calculate(Map<String, Float> items, Float amount) throws InsufficientAmountException {
        if (items == null || amount == null || items.isEmpty()) {
            return null;
        }

        Map<String, Integer> intItems = items
                .entrySet()
                .stream()
                .filter(map -> map.getValue() <= amount)
                .collect(Collectors.toMap(Map.Entry::getKey, map -> (int) (map.getValue() * DECIMALS)));

        if (intItems.size() == 0) {
            throw new InsufficientAmountException(amount);
        }

        String[] itemsId = intItems.keySet().toArray(new String[0]);
        int[] itemsPrice = intItems.values().stream().mapToInt(Integer::intValue).toArray();

        if (itemsPrice.length == 1 && itemsPrice[0] <= amount * DECIMALS) {
            return new ArrayList<>(Collections.singletonList(itemsId[0]));
        }

        int itemsAmount = itemsPrice.length;
        int couponValue = (int) (amount * DECIMALS);
        int[][] bestSoFar = new int[itemsAmount + 1][couponValue + 1];

        for (int price = 1; price <= itemsAmount; price++) {
            for (int value = 1; value <= couponValue; value++) {
                if (itemsPrice[price - 1] <= value) {
                    bestSoFar[price][value] = Math.max(itemsPrice[price - 1] + bestSoFar[price - 1][value - itemsPrice[price - 1]], bestSoFar[price - 1][value]);
                } else {
                    bestSoFar[price][value] = bestSoFar[price - 1][value];
                }
            }
        }

        return getItemsForBestSolution(itemsId, itemsPrice, couponValue, bestSoFar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Float calculateTotalAmount(List<String> ids, Map<String, Float> items) {
        if (ids == null || items == null) {
            return null;
        }

        float sum = 0;
        for (String id : ids) {
            Float price = items.get(id);

            if (price == null) {
                return null;
            }

            sum += price;
        }

        return sum;
    }

    /**
     * Retrieves the elements that are part of the best solution found, sorted by the IDs.
     *
     * @param itemsId     an array of items ID.
     * @param itemsPrice  an array of items price.
     * @param couponValue the value of the coupon.
     * @param bestSoFar   the matrix containing the solutions.
     * @return a list of the items that make up the best solution found.
     */
    public List<String> getItemsForBestSolution(String[] itemsId, int[] itemsPrice, int couponValue, int[][] bestSoFar) {
        int value = couponValue;
        int best = bestSoFar[itemsPrice.length][couponValue];
        List<String> items = new ArrayList<>();

        for (int i = itemsPrice.length; i > 0 && best > 0; i--) {
            if (best != bestSoFar[i - 1][value]) {
                items.add(itemsId[i - 1]);
                best -= itemsPrice[i - 1];
                value -= itemsPrice[i - 1];
            }
        }

        items.sort(String::compareTo);
        return items;
    }
}
