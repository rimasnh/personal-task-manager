package com.personal.taskmanager.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class AiConfiguration {

    @Bean
    TaskSuggestionClient taskSuggestionClient(OpenAiProperties properties, ObjectMapper objectMapper) {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            return new FallbackTaskSuggestionClient();
        }

        RestClient restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader("Authorization", "Bearer " + properties.apiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();

        return new OpenAiTaskSuggestionClient(restClient, objectMapper, properties.model());
    }
}

