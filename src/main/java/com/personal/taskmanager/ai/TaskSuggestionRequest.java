package com.personal.taskmanager.ai;

import jakarta.validation.constraints.NotBlank;

public record TaskSuggestionRequest(
        @NotBlank(message = "Description is required")
        String description
) {
}

