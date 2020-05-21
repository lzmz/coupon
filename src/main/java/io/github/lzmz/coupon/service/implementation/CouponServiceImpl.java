package io.github.lzmz.coupon.service.implementation;

import io.github.lzmz.coupon.service.CouponService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<String> calculate(Map<String, Float> items, Float amount) {
        if (items == null) {
            return null;
        }

        Map<String, Integer> intItems = items
                .entrySet()
                .stream()
                .filter(map -> map.getValue() <= amount)
                .collect(Collectors.toMap(map -> map.getKey(), map -> (int) (map.getValue().floatValue() * DECIMALS)));

        String[] itemsId = intItems.keySet().toArray(new String[intItems.size()]);
        int[] itemsPrice = intItems.values().stream().mapToInt(Integer::intValue).toArray();

        if (itemsPrice.length == 0) {
            return null;
        }

        if (itemsPrice.length == 1 && itemsPrice[0] <= amount) {
            return Arrays.asList(itemsId[0], String.valueOf(itemsPrice[0] / DECIMALS));
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

        return getItemsForBestResult(itemsId, itemsPrice, couponValue, bestSoFar);
    }

    /**
     * Recover the elements that are part of the best solution and includes the maximized
     * total expense in the last position of the list.
     *
     * @param itemsId     an array of items ID.
     * @param itemsPrice  an array of items price.
     * @param couponValue the value of the coupon.
     * @param bestSoFar   the matrix containing the solutions.
     * @return a list of the items that make up the solution found and the maximized total expense.
     */
    public List<String> getItemsForBestResult(String[] itemsId, int[] itemsPrice, int couponValue, int[][] bestSoFar) {
        int value = couponValue;
        int best = bestSoFar[itemsPrice.length][couponValue];
        List<String> candidates = new ArrayList<>();

        for (int i = itemsPrice.length; i > 0 && best > 0; i--) {
            if (best != bestSoFar[i - 1][value]) {
                candidates.add(itemsId[i - 1]);
                best -= itemsPrice[i - 1];
                value -= itemsPrice[i - 1];
            }
        }

        candidates.add(String.valueOf(bestSoFar[itemsPrice.length][couponValue] / DECIMALS));
        return candidates;
    }
}
