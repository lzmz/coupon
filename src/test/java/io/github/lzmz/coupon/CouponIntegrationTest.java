package io.github.lzmz.coupon;

import io.github.lzmz.coupon.dto.request.CouponCalculateDto;
import io.github.lzmz.coupon.dto.response.CouponSolutionDto;
import io.github.lzmz.coupon.endpoint.CouponEndpoint;
import io.github.lzmz.coupon.exceptions.ApiError;
import io.github.lzmz.coupon.exceptions.ApiErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void handleNoHandlerFoundException_invalidHandler_shouldReturnNotFound() {
        ResponseEntity<ApiError> response = testRestTemplate.getForEntity("/invalid-endpoint", ApiError.class);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(ApiErrorCode.NOT_FOUND, Objects.requireNonNull(response.getBody()).getCode());
    }

    @Test
    public void handleHttpMediaTypeNotSupported_invalidMediaType_shouldReturnUnsupportedMediaType() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_STREAM_JSON);
        HttpEntity<CouponCalculateDto> httpEntity = new HttpEntity<>(new CouponCalculateDto(), headers);

        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(CouponEndpoint.BASE, httpEntity, ApiError.class);
        assertEquals(response.getStatusCode(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertEquals(ApiErrorCode.UNSUPPORTED_MEDIA_TYPE, Objects.requireNonNull(response.getBody()).getCode());
    }

    @Test
    public void handleHttpRequestMethodNotSupported_invalidMethod_shouldReturnMethodNotAllowed() {
        ResponseEntity<ApiError> response = testRestTemplate.getForEntity(CouponEndpoint.BASE, ApiError.class);
        assertEquals(response.getStatusCode(), HttpStatus.METHOD_NOT_ALLOWED);
        assertEquals(ApiErrorCode.METHOD_NOT_ALLOWED, Objects.requireNonNull(response.getBody()).getCode());
    }

    @Test
    public void handleMethodArgumentNotValid_invalidArgument_shouldReturnBadRequest() {
        CouponCalculateDto couponCalculateDto = new CouponCalculateDto(null, null);
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(CouponEndpoint.BASE, couponCalculateDto, ApiError.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(ApiErrorCode.METHOD_ARGUMENT_NOT_VALID, Objects.requireNonNull(response.getBody()).getCode());
    }

    @Test
    public void handleWebClientResponseException_externalServiceCallFailure_shouldReturnBadGateway() {
        CouponCalculateDto couponCalculateDto = new CouponCalculateDto(Collections.singletonList("not-valid"), 100F);
        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(CouponEndpoint.BASE, couponCalculateDto, ApiError.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_GATEWAY);
        assertEquals(ApiErrorCode.WEB_CLIENT_RESPONSE, Objects.requireNonNull(response.getBody()).getCode());
    }

    @Test
    public void handleHttpMessageNotReadable_invalidBody_shouldReturnBadRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "\"amount\": invalid";
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(CouponEndpoint.BASE, httpEntity, ApiError.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(ApiErrorCode.MESSAGE_NOT_READABLE, Objects.requireNonNull(response.getBody()).getCode());
    }

    @Test
    public void calculate_validBody_shouldReturnOkAndSolution() {
        List<String> ids = Collections.singletonList("MLA805107769");
        CouponCalculateDto couponCalculateDto = new CouponCalculateDto(ids, 500F);

        ResponseEntity<CouponSolutionDto> response = testRestTemplate.postForEntity(CouponEndpoint.BASE, couponCalculateDto, CouponSolutionDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ids.size(), Objects.requireNonNull(response.getBody()).getItemsId().size());
        assertIterableEquals(ids, response.getBody().getItemsId());
    }

    @Test
    public void calculate_validBodyInsufficientAmount_shouldReturnNotFound() {
        List<String> ids = Collections.singletonList("MLA805107769");
        CouponCalculateDto couponCalculateDto = new CouponCalculateDto(ids, 0F);

        ResponseEntity<ApiError> response = testRestTemplate.postForEntity(CouponEndpoint.BASE, couponCalculateDto, ApiError.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ApiErrorCode.INSUFFICIENT_AMOUNT, Objects.requireNonNull(response.getBody()).getCode());
    }
}