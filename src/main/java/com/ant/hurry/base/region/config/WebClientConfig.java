package com.ant.hurry.base.region.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://grpc-proxy-server-mkvo6j4wsq-du.a.run.app")
                .build();
    }
}