package io.github.lzmz.coupon.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Class to handle errors on external service calls.
 */
@Getter
@Setter
@AllArgsConstructor
public class ConsumerServiceException extends Exception {

    /**
     * The error message provided by the external service.
     */
    private final String message;
}