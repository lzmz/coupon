package io.github.lzmz.coupon.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class NoItemPriceException extends Exception {

    /**
     * The item IDs by which the exception was thrown.
     */
    private final List<String> ids;

    public NoItemPriceException(String id) {
        this.ids = Collections.singletonList(id);
    }
}