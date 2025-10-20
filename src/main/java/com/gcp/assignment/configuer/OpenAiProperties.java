package com.gcp.assignment.configuer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.ai.openai")
public class OpenAiProperties {
    private String apiKey;
    private String chatBaseUrl;

}