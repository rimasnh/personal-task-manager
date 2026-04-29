package com.personal.taskmanager.api;

import com.personal.taskmanager.ai.TaskSuggestionRequest;
import com.personal.taskmanager.ai.TaskSuggestionResponse;
import com.personal.taskmanager.ai.TaskSuggestionService;
import com.personal.taskmanager.task.TaskRequest;
import com.personal.taskmanager.task.TaskResponse;
import com.personal.taskmanager.task.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskSuggestionService taskSuggestionService;

    public TaskController(TaskService taskService, TaskSuggestionService taskSuggestionService) {
        this.taskService = taskService;
        this.taskSuggestionService = taskSuggestionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody TaskRequest request) {
        return taskService.createTask(request);
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(
            @RequestParam(required = false) LocalDate dueDate,
            @RequestParam(required = false) com.personal.taskmanager.task.Priority priority,
            @RequestParam(required = false) com.personal.taskmanager.task.TaskStatus status
    ) {
        return taskService.getAllTasks(dueDate, priority, status);
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/suggest")
    public TaskSuggestionResponse suggestTask(@Valid @RequestBody TaskSuggestionRequest request) {
        return taskSuggestionService.suggestTask(request);
    }
}
