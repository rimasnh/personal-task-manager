package com.personal.taskmanager.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

public class OpenAiTaskSuggestionClient implements TaskSuggestionClient {

    private static final String SYSTEM_PROMPT = """
            Convert the user's plain-language request into a JSON task object.
            Return only valid JSON with these keys:
            title, description, dueDate, priority, status.
            Use dueDate in ISO-8601 format or null.
            Allowed priority values: LOW, MEDIUM, HIGH.
            Allowed status values: TODO, IN_PROGRESS, DONE.
            """;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public OpenAiTaskSuggestionClient(RestClient restClient, ObjectMapper objectMapper, String model) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.model = model;
    }

    @Override
    public TaskSuggestionResponse suggestTask(String description) {
        Map<String, Object> payload = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", description)
                ),
                "temperature", 0.2
        );

        try {
            String responseBody = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(String.class);

            String content = extractContent(responseBody);
            TaskSuggestionResponse parsed = objectMapper.readValue(content, TaskSuggestionResponse.class);
            return new TaskSuggestionResponse(
                    parsed.title(),
                    parsed.description(),
                    parsed.dueDate(),
                    parsed.priority(),
                    parsed.status(),
                    "openai"
            );
        } catch (RestClientException | JsonProcessingException e) {
            throw new TaskSuggestionException("Unable to generate a task suggestion from the AI model", e);
        }
    }

    private String extractContent(String responseBody) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(responseBody);
        JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
        if (contentNode.isMissingNode() || contentNode.asText().isBlank()) {
            throw new TaskSuggestionException("AI model returned an empty response");
        }
        return contentNode.asText();
    }
}

