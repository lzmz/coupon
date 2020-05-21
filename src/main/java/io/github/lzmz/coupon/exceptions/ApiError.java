package io.github.lzmz.coupon.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * Class to handle API errors.
 */
@Getter
@Setter
public class ApiError implements Serializable {

    /**
     * An internal code of the error.
     */
    @Schema(description = "Internal code of the error.")
    private int code;

    /**
     * The operation call status.
     */
    @Schema(description = "HTTP status response code.")
    private int status;

    /**
     * The {@link LocalDateTime} instance of when the error happened. It is established
     * by default when a new {@link ApiError} is created.
     */
    @Schema(description = "Date time of the error.")
    private LocalDateTime timestamp;

    /**
     * Message about the error.
     */
    @Schema(description = "Message about the error.")
    private String message;

    /**
     * List of detailed errors.
     */
    @Schema(description = "Detailed error.")
    private List<String> errors;

    public ApiError(int code, HttpStatus status, String message, List<String> errors) {
        super();
        this.code = code;
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.errors = errors;
    }

    public ApiError(int code, HttpStatus status, String message, String error) {
        super();
        this.code = code;
        this.status = status.value();
        this.timestamp = LocalDateTime.now();
        this.message = message;
        errors = Collections.singletonList(error);
    }
}
