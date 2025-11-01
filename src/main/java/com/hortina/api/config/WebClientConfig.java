package com.hortina.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient plantApiClient(
            WebClient.Builder builder,
            @Value("${plant.base-url}") String baseUrl,
            @Value("${plant.api-key-id}") String apiKeyId,
            @Value("${plant.api-key-secret}") String apiKeySecret) {

        return builder
                .baseUrl(baseUrl)
                .defaultHeader("x-permapeople-key-id", apiKeyId)
                .defaultHeader("x-permapeople-key-secret", apiKeySecret)
                .build();
    }
}
