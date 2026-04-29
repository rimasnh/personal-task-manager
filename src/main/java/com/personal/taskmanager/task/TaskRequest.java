package com.personal.taskmanager.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        String title,
        String description,
        LocalDate startDate,
        LocalDate dueDate,
        @NotNull(message = "Priority is required")
        Priority priority,
        @NotNull(message = "Status is required")
        TaskStatus status
) {
}
