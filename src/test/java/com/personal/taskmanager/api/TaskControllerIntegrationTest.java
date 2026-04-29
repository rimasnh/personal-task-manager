package com.personal.taskmanager.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.taskmanager.ai.TaskSuggestionClient;
import com.personal.taskmanager.ai.TaskSuggestionResponse;
import com.personal.taskmanager.task.Priority;
import com.personal.taskmanager.task.TaskRequest;
import com.personal.taskmanager.task.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.seed.enabled=false")
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskSuggestionClient taskSuggestionClient;

    @Test
    void crudEndpointsWorkEndToEnd() throws Exception {
        TaskRequest createRequest = new TaskRequest(
                "Book mock interview",
                "Schedule a mock interview for next week",
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 4),
                Priority.MEDIUM,
                TaskStatus.TODO
        );

        String createResponse = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Book mock interview"))
                .andExpect(jsonPath("$.startDate").value("2026-05-01"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long taskId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/tasks")
                        .param("dueDate", "2026-05-04")
                        .param("priority", "MEDIUM")
                        .param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("TODO"));

        TaskRequest updateRequest = new TaskRequest(
                "Book mock interview",
                "Schedule a mock interview with a recruiter",
                LocalDate.of(2026, 5, 2),
                LocalDate.of(2026, 5, 6),
                Priority.HIGH,
                TaskStatus.IN_PROGRESS
        );

                mockMvc.perform(put("/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2026-05-02"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(delete("/tasks/{id}", taskId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void suggestEndpointReturnsMockedAiResponse() throws Exception {
        TaskSuggestionResponse suggestion = new TaskSuggestionResponse(
                "Submit report",
                "Submit the quarterly report before Friday",
                LocalDate.of(2026, 5, 1),
                Priority.HIGH,
                TaskStatus.TODO,
                "openai"
        );

        when(taskSuggestionClient.suggestTask("remind me to submit the quarterly report before Friday"))
                .thenReturn(suggestion);

        mockMvc.perform(post("/tasks/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "remind me to submit the quarterly report before Friday"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Submit report"))
                .andExpect(jsonPath("$.source").value("openai"));
    }
}
