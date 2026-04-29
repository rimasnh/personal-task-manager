package com.personal.taskmanager.ai;

import com.personal.taskmanager.task.Priority;
import com.personal.taskmanager.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskSuggestionServiceTest {

    @Mock
    private TaskSuggestionClient taskSuggestionClient;

    @InjectMocks
    private TaskSuggestionService taskSuggestionService;

    @Test
    void suggestTaskDelegatesToClient() {
        TaskSuggestionResponse suggestion = new TaskSuggestionResponse(
                "Submit quarterly report",
                "Submit the quarterly report before Friday",
                LocalDate.of(2026, 5, 1),
                Priority.HIGH,
                TaskStatus.TODO,
                "openai"
        );

        when(taskSuggestionClient.suggestTask("submit the quarterly report before Friday"))
                .thenReturn(suggestion);

        TaskSuggestionResponse response = taskSuggestionService
                .suggestTask(new TaskSuggestionRequest("submit the quarterly report before Friday"));

        assertThat(response.title()).isEqualTo("Submit quarterly report");
        assertThat(response.priority()).isEqualTo(Priority.HIGH);
    }
}

