package com.personal.taskmanager.task;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(TaskRequest request) {
        Task task = mapToTask(request, new Task());
        return TaskResponse.from(taskRepository.save(task));
    }

    public List<TaskResponse> getAllTasks(LocalDate dueDate, Priority priority, TaskStatus status) {
        return taskRepository.findAll()
                .stream()
                .filter(task -> dueDate == null || dueDate.equals(task.getDueDate()))
                .filter(task -> priority == null || priority == task.getPriority())
                .filter(task -> status == null || status == task.getStatus())
                .map(TaskResponse::from)
                .toList();
    }

    public TaskResponse getTaskById(Long id) {
        return TaskResponse.from(findTask(id));
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task existingTask = findTask(id);
        mapToTask(request, existingTask);
        return TaskResponse.from(taskRepository.save(existingTask));
    }

    public void deleteTask(Long id) {
        Task existingTask = findTask(id);
        taskRepository.delete(existingTask);
    }

    private Task findTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    private Task mapToTask(TaskRequest request, Task task) {
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setStartDate(request.startDate());
        task.setDueDate(request.dueDate());
        task.setPriority(request.priority());
        task.setStatus(request.status());
        return task;
    }
}
