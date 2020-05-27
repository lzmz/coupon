package io.github.lzmz.coupon.service.implementation;

import io.github.lzmz.coupon.exception.InsufficientAmountException;
import io.github.lzmz.coupon.service.CouponService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CouponServiceImpl implements CouponService {

    /**
     * The value by which prices will be multiplied to consider pennies
     * (e.g. if the value is 100, two pennies will be considered).
     */
    private final static float DECIMALS = 100F;

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

        if (intItems.size() == 1) {
            return new ArrayList<>(intItems.keySet());
        }

        String[] ids = intItems.keySet().toArray(new String[0]);
        int[] prices = intItems.values().stream().mapToInt(Integer::intValue).toArray();

        int itemsNumber = ids.length;
        int couponValue = (int) (amount * DECIMALS);
        int[][] solutions = new int[itemsNumber + 1][couponValue + 1];

        for (int itemCount = 1; itemCount <= itemsNumber; itemCount++) {
            for (int value = 1; value <= couponValue; value++) {
                if (prices[itemCount - 1] > value) {
                    solutions[itemCount][value] = solutions[itemCount - 1][value];
                } else {
                    int notConsidered = solutions[itemCount - 1][value];
                    int considered = prices[itemCount - 1] + solutions[itemCount - 1][value - prices[itemCount - 1]];
                    solutions[itemCount][value] = Math.max(notConsidered, considered);
                }
            }
        }

        return getItemsForBestSolution(ids, prices, couponValue, solutions);
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
     * @param solutions   the matrix containing the solutions.
     * @return a list of the items that make up the best solution found.
     */
    public List<String> getItemsForBestSolution(String[] itemsId, int[] itemsPrice, int couponValue, int[][] solutions) {
        int value = couponValue;
        int best = solutions[itemsPrice.length][couponValue];
        List<String> items = new ArrayList<>();

        for (int itemCount = itemsPrice.length; itemCount > 0 && best > 0; itemCount--) {
            if (best != solutions[itemCount - 1][value]) {
                items.add(itemsId[itemCount - 1]);
                best -= itemsPrice[itemCount - 1];
                value -= itemsPrice[itemCount - 1];
            }
        }

        items.sort(String::compareTo);
        return items;
    }
}
