package io.github.lzmz.coupon.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InsufficientAmountException extends Exception {

    /**
     * The insufficient amount by which the exception was thrown.
     */
    private final float amount;
}