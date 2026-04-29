package com.personal.taskmanager.ai;

import com.personal.taskmanager.task.Priority;
import com.personal.taskmanager.task.TaskStatus;

import java.time.LocalDate;

public record TaskSuggestionResponse(
        String title,
        String description,
        LocalDate dueDate,
        Priority priority,
        TaskStatus status,
        String source
) {
}

