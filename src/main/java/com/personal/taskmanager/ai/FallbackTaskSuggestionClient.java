package com.personal.taskmanager.ai;

import com.personal.taskmanager.task.Priority;
import com.personal.taskmanager.task.TaskStatus;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class FallbackTaskSuggestionClient implements TaskSuggestionClient {

    @Override
    public TaskSuggestionResponse suggestTask(String description) {
        String normalized = description.trim();
        LocalDate dueDate = inferDueDate(normalized);
        Priority priority = normalized.toLowerCase().contains("urgent")
                || normalized.toLowerCase().contains("asap")
                || normalized.toLowerCase().contains("today")
                ? Priority.HIGH
                : Priority.MEDIUM;

        String title = normalized.length() <= 60
                ? normalized
                : normalized.substring(0, 57) + "...";

        return new TaskSuggestionResponse(
                capitalize(title),
                normalized,
                dueDate,
                priority,
                TaskStatus.TODO,
                "fallback"
        );
    }

    private LocalDate inferDueDate(String description) {
        String lowerCase = description.toLowerCase();
        if (lowerCase.contains("today")) {
            return LocalDate.now();
        }
        if (lowerCase.contains("tomorrow")) {
            return LocalDate.now().plusDays(1);
        }
        if (lowerCase.contains("friday")) {
            return LocalDate.now().with(java.time.temporal.TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        }
        return null;
    }

    private String capitalize(String value) {
        if (value.isBlank()) {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}

