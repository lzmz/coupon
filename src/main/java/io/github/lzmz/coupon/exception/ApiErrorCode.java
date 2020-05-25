package io.github.lzmz.coupon.exception;

public final class ApiErrorCode {
    public static final int INTERNAL_SERVER_ERROR = 0;
    public static final int NOT_FOUND = 20;
    public static final int UNSUPPORTED_MEDIA_TYPE = 40;
    public static final int METHOD_NOT_ALLOWED = 41;
    public static final int METHOD_ARGUMENT_NOT_VALID = 42;
    public static final int MESSAGE_NOT_READABLE = 60;
    public static final int WEB_CLIENT_RESPONSE = 80;
    public static final int NO_ITEM_PRICE = 100;
    public static final int INSUFFICIENT_AMOUNT = 120;

    private ApiErrorCode() {
    }
}
