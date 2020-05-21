package io.github.lzmz.coupon.config;

import io.github.lzmz.coupon.endpoint.ItemConsumerEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient itemWebClient() {
        return WebClient.builder()
                .baseUrl(ItemConsumerEndpoint.BASE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
