package io.github.lzmz.coupon.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.SpringDocConfigProperties;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    private static final String JSON_MEDIA_TYPE = org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
    private static final String BASE_PACKAGE = "io.github.lzmz.coupon.controller";

    /**
     * Defines global configurations for the exposed API.
     *
     * @param springDocConfigProperties {@link SpringDocConfigProperties}.
     * @return {@link BeanFactoryPostProcessor}.
     */
    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor(SpringDocConfigProperties springDocConfigProperties) {
        return beanFactory -> {
            springDocConfigProperties.setPackagesToScan(Collections.singletonList(BASE_PACKAGE));
            springDocConfigProperties.setDefaultProducesMediaType(JSON_MEDIA_TYPE);
            springDocConfigProperties.setDefaultConsumesMediaType(JSON_MEDIA_TYPE);
        };
    }

    /**
     * Creates the exposed API.
     *
     * @return {@link OpenAPI}.
     */
    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(info());
    }

    /**
     * Defines metadata about the API.
     *
     * @return the metadata about the API.
     */
    private Info info() {
        return new Info()
                .title("Coupon REST API")
                .version("v1.0.0")
                .contact(contact())
                .license(license());
    }

    /**
     * Defines the contact information for the exposed API.
     *
     * @return the contact information.
     */
    private Contact contact() {
        return new Contact().name("Leonel Menendez")
                .email("leonelemenendez@gmail.com")
                .url("https://www.linkedin.com/in/leonel-menendez/");
    }

    /**
     * Defines the license information for the exposed API.
     *
     * @return the license information.
     */
    private License license() {
        return new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html");
    }
}
