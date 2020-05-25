package io.github.lzmz.coupon.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.lzmz.coupon.dto.response.ConsumerServiceErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Class to handle errors appropriately to be sent as responses.
 */
@ControllerAdvice
public class RestExceptionHandler {

    private final ObjectMapper objectMapper;

    public RestExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Retrieves a new API error response entity.
     *
     * @param code    the internal code.
     * @param status  the HTTP status.
     * @param message the error message.
     * @param errors  a list of detailed errors.
     * @return a new {@link ApiError}.
     */
    public ResponseEntity<ApiError> getErrorResponse(int code, HttpStatus status, String message, List<String> errors) {
        ApiError apiError = new ApiError(code, status, message.trim(), errors);
        return new ResponseEntity<>(apiError, new HttpHeaders(), status);
    }

    /**
     * Retrieves a new API error response entity.
     *
     * @param code    the internal code.
     * @param status  the HTTP status.
     * @param message the error message.
     * @param error   the detailed errors.
     * @return a new {@link ApiError}.
     */
    public ResponseEntity<ApiError> getErrorResponse(int code, HttpStatus status, String message, String error) {
        ApiError apiError = new ApiError(code, status, message.trim(), error.trim());
        return new ResponseEntity<>(apiError, new HttpHeaders(), status);
    }

    /**
     * Triggered when the requested resource couldn't be found.
     *
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiError> handleNoHandlerFoundException() {
        int code = ApiErrorCode.NOT_FOUND;
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String message = "404 error";
        String error = "Requested resource couldn't be found";
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Triggered when an unsupported media type for a given end point was received.
     *
     * @param ex the {@link HttpMediaTypeNotSupportedException} to handle.
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        int code = ApiErrorCode.UNSUPPORTED_MEDIA_TYPE;
        HttpStatus httpStatus = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        String message = "Unsupported media type";
        StringBuilder error = new StringBuilder();
        error.append(ex.getContentType()).append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(mediaType -> error.append(mediaType).append(", "));
        return getErrorResponse(code, httpStatus, message, error.toString());
    }

    /**
     * Triggered when an unsupported HTTP method for a given end point was received.
     *
     * @param ex the {@link HttpRequestMethodNotSupportedException} to handle.
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        int code = ApiErrorCode.METHOD_NOT_ALLOWED;
        HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
        String message = "Unsupported HTTP method";
        StringBuilder error = new StringBuilder();
        error.append(ex.getMethod()).append(" method is not supported for this request. Supported methods are ");
        Objects.requireNonNull(ex.getSupportedHttpMethods()).forEach(method -> error.append(method).append(" "));
        return getErrorResponse(code, httpStatus, message, error.toString());
    }

    /**
     * Triggered when an argument annotated with {@code @Valid} failed on being validated.
     *
     * @param ex the {@link MethodArgumentNotValidException} to handle.
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        int code = ApiErrorCode.METHOD_ARGUMENT_NOT_VALID;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        StringBuilder message = new StringBuilder("Invalid arguments: ");
        List<String> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
            message.append(error.getField()).append(" ");
        }

        return getErrorResponse(code, httpStatus, message.toString(), errors);
    }

    /**
     * Triggered when a fatal problem occurred with content mapping.
     *
     * @param ex the {@link HttpMessageNotReadableException} to handle.
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        int code = ApiErrorCode.MESSAGE_NOT_READABLE;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "Invalid body";
        String error = ((JsonProcessingException) ex.getCause()).getOriginalMessage();
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Triggered when an external service call failed.
     *
     * @param ex the {@link WebClientResponseException} to handle.
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiError> handleWebClientResponseException(WebClientResponseException ex) throws JsonProcessingException {
        int code = ApiErrorCode.WEB_CLIENT_RESPONSE;
        HttpStatus httpStatus = HttpStatus.BAD_GATEWAY;
        String message = "Error occurred on external call";
        String body = ex.getResponseBodyAsString();
        String error = objectMapper.readValue(body, ConsumerServiceErrorDto.class).getMessage();
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Triggered when one or more items of a given list of IDs has no associated price.
     *
     * @param ex the exception to handle.
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NoItemPriceException.class})
    public ResponseEntity<ApiError> handleNoItemPrice(NoItemPriceException ex) {
        int code = ApiErrorCode.NO_ITEM_PRICE;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String message = "No item price";
        List<String> errors = ex.getIds().stream().map(id -> id + " has no price").collect(Collectors.toList());
        return getErrorResponse(code, httpStatus, message, errors);
    }

    /**
     * Triggered when a given amount was not enough to buy at least one item.
     *
     * @param ex the exception to handle.
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({InsufficientAmountException.class})
    public ResponseEntity<ApiError> handleInsufficientAmount(InsufficientAmountException ex) {
        int code = ApiErrorCode.INSUFFICIENT_AMOUNT;
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        String message = "Insufficient amount";
        String error = "The amount " + ex.getAmount() + " is not enough to buy at least one item";
        return getErrorResponse(code, httpStatus, message, error);
    }

    /**
     * Default Handler. It deals with all other exceptions that don't have specific handlers.
     *
     * @return a {@link ResponseEntity} object with the error handled.
     */
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiError> handleAll() {
        int code = ApiErrorCode.INTERNAL_SERVER_ERROR;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "It's not you. It's us. We are having some problems";
        String error = "Error occurred";
        return getErrorResponse(code, httpStatus, message, error);
    }
}
