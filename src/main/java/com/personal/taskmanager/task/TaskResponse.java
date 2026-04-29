package com.personal.taskmanager.task;

import java.time.LocalDate;

public record TaskResponse(
        Long id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate dueDate,
        Priority priority,
        TaskStatus status
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStartDate(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus()
        );
    }
}
