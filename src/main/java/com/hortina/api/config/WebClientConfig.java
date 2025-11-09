package com.hortina.api.config;

import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;


@Configuration
public class WebClientConfig {

    @Bean
    public WebClient plantApiClient(
            WebClient.Builder builder,
            @Value("${plant.base-url}") String baseUrl,
            @Value("${plant.api-key-id}") String apiKeyId,
            @Value("${plant.api-key-secret}") String apiKeySecret) {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)));

        return builder
                .baseUrl(baseUrl)
                .defaultHeader("x-permapeople-key-id", apiKeyId)
                .defaultHeader("x-permapeople-key-secret", apiKeySecret)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
