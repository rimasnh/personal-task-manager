package com.personal.taskmanager.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskRequest taskRequest;
    private Task task;

    @BeforeEach
    void setUp() {
        taskRequest = new TaskRequest(
                "Finish portfolio",
                "Polish the project readme",
                LocalDate.of(2026, 4, 28),
                LocalDate.of(2026, 5, 1),
                Priority.HIGH,
                TaskStatus.TODO
        );

        task = new Task();
        task.setId(1L);
        task.setTitle(taskRequest.title());
        task.setDescription(taskRequest.description());
        task.setStartDate(taskRequest.startDate());
        task.setDueDate(taskRequest.dueDate());
        task.setPriority(taskRequest.priority());
        task.setStatus(taskRequest.status());
    }

    @Test
    void createTaskReturnsSavedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(taskRequest);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.title()).isEqualTo("Finish portfolio");
    }

    @Test
    void getAllTasksReturnsMappedTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponse> responses = taskService.getAllTasks(null, null, null);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).status()).isEqualTo(TaskStatus.TODO);
    }

    @Test
    void getTaskByIdReturnsMappedTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1L);

        assertThat(response.description()).isEqualTo("Polish the project readme");
    }

    @Test
    void updateTaskReturnsUpdatedTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskRequest updateRequest = new TaskRequest(
                "Finish portfolio v2",
                "Add API examples",
                LocalDate.of(2026, 4, 29),
                LocalDate.of(2026, 5, 2),
                Priority.MEDIUM,
                TaskStatus.IN_PROGRESS
        );

        TaskResponse response = taskService.updateTask(1L, updateRequest);

        assertThat(response.title()).isEqualTo("Finish portfolio v2");
        assertThat(response.status()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void getAllTasksAppliesFilters() {
        Task secondTask = new Task();
        secondTask.setId(2L);
        secondTask.setTitle("Start interview prep");
        secondTask.setDescription("Review interview questions");
        secondTask.setStartDate(LocalDate.of(2026, 4, 30));
        secondTask.setDueDate(LocalDate.of(2026, 5, 3));
        secondTask.setPriority(Priority.LOW);
        secondTask.setStatus(TaskStatus.DONE);

        when(taskRepository.findAll()).thenReturn(List.of(task, secondTask));

        List<TaskResponse> responses = taskService.getAllTasks(
                LocalDate.of(2026, 5, 1),
                Priority.HIGH,
                TaskStatus.TODO
        );

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).startDate()).isEqualTo(LocalDate.of(2026, 4, 28));
    }

    @Test
    void deleteTaskRemovesExistingTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
    }
}
