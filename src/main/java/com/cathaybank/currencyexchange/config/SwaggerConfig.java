package com.cathaybank.currencyexchange.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Currency Exchange API")
                        .description("API for currency exchange rates management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Cathay United Bank")
                                .url("https://www.cathaybk.com.tw/")
                                .email("info@cathaybk.com.tw"))
                        .license(new License()
                                .name("Private")
                                .url("https://www.cathaybk.com.tw/terms"))
                );
    }
}