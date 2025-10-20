package com.gcp.assignment.configuer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GeminiConfig {

    @Bean
    public RestClient geminiClient(GeminiProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getUrl())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("x-goog-api-key", properties.getApiKey())
                .build();
    }
}
