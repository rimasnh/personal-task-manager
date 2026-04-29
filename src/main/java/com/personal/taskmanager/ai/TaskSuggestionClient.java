package com.personal.taskmanager.ai;

public interface TaskSuggestionClient {

    TaskSuggestionResponse suggestTask(String description);
}

