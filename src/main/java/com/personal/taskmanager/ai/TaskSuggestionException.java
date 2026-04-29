package com.personal.taskmanager.ai;

public class TaskSuggestionException extends RuntimeException {

    public TaskSuggestionException(String message) {
        super(message);
    }

    public TaskSuggestionException(String message, Throwable cause) {
        super(message, cause);
    }
}

