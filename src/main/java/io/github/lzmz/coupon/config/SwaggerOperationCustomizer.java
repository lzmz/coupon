package io.github.lzmz.coupon.config;

import io.github.lzmz.coupon.exceptions.ApiError;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerOperationCustomizer implements OperationCustomizer {

    private static final Schema API_ERROR_SCHEMA;
    private static final MediaType API_ERROR_MEDIA_TYPE;
    private static final Content API_ERROR_CONTENT;

    static {
        API_ERROR_SCHEMA = new Schema<>();
        API_ERROR_SCHEMA.set$ref(ApiError.class.getSimpleName());
        API_ERROR_MEDIA_TYPE = new MediaType().schema(API_ERROR_SCHEMA);
        API_ERROR_CONTENT = new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE, API_ERROR_MEDIA_TYPE);
    }

    /**
     * Customizes all the operations of the exposed API with default responses.
     *
     * @param operation     {@link Operation}.
     * @param handlerMethod {@link HandlerMethod}.
     * @return the customized {@link Operation}.
     */
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        addDefaultGlobalResponses(operation.getResponses());
        return operation;
    }

    /**
     * Adds the default global API responses.
     */
    private void addDefaultGlobalResponses(ApiResponses apiResponses) {
        apiResponses.replace(String.valueOf(HttpStatus.BAD_REQUEST.value()), getDefaultGlobalResponse(HttpStatus.BAD_REQUEST));
        apiResponses.put(String.valueOf(HttpStatus.NOT_FOUND.value()), getDefaultGlobalResponse(HttpStatus.NOT_FOUND));
        apiResponses.replace(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), getDefaultGlobalResponse(HttpStatus.INTERNAL_SERVER_ERROR));
        apiResponses.replace(String.valueOf(HttpStatus.BAD_GATEWAY.value()), getDefaultGlobalResponse(HttpStatus.BAD_GATEWAY));
    }

    /**
     * Returns a default global response for the given {@link HttpStatus}.
     * <p>If the given status is a 4xx client error or a 5xx server error, it will bind the {@link ApiError} to the schema of the response.</p>
     *
     * @param httpStatus the status of the global response.
     * @return the default global response.
     */
    private ApiResponse getDefaultGlobalResponse(HttpStatus httpStatus) {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.description(httpStatus.getReasonPhrase());

        if (httpStatus.is4xxClientError() || httpStatus.is5xxServerError()) {
            apiResponse.content(API_ERROR_CONTENT);
        }

        return apiResponse;
    }
}
