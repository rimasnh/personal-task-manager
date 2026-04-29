package com.personal.taskmanager.ai;

import org.springframework.stereotype.Service;

@Service
public class TaskSuggestionService {

    private final TaskSuggestionClient taskSuggestionClient;

    public TaskSuggestionService(TaskSuggestionClient taskSuggestionClient) {
        this.taskSuggestionClient = taskSuggestionClient;
    }

    public TaskSuggestionResponse suggestTask(TaskSuggestionRequest request) {
        return taskSuggestionClient.suggestTask(request.description());
    }
}

