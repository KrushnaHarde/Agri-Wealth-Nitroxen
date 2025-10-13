package com.nitroxen.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitroxen.demo.config.JwtAuthenticationFilter;
import com.nitroxen.demo.config.JwtService;
import com.nitroxen.demo.config.SecurityConfiguration;
import com.nitroxen.demo.dto.request.WhatsAppTaskAssignRequest;
import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.service.TaskManagerService;
import com.nitroxen.demo.service.UserService;
import com.nitroxen.demo.service.WhatsAppService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TaskManagerController.class)
public class WhatsAppTaskAssignmentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskManagerService taskManagerService;

    @MockBean
    private WhatsAppService whatsAppService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAssignTaskWithWhatsApp_Success() throws Exception {
        // Arrange
        WhatsAppTaskAssignRequest request = new WhatsAppTaskAssignRequest();
        request.setTaskName("Clean greenhouse");
        request.setWorkerId(1L);
        request.setDescription("Clean all windows");
        request.setDeadline("2025-10-15");

        Task mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Clean greenhouse");
        mockTask.setWorkerId(1L);

        when(taskManagerService.getTasksByWorkerId(1L)).thenReturn(Arrays.asList(mockTask));
        doNothing().when(taskManagerService).assignTask(anyString(), anyString(), anyLong(), anyString(), any());
        doNothing().when(whatsAppService).sendTaskAssignmentMessage(any(Task.class));

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task assigned successfully and WhatsApp notification sent"));

        // Verify interactions
        verify(taskManagerService).assignTask("Clean greenhouse", "Clean all windows", 1L, "2025-10-15", null);
        verify(taskManagerService).getTasksByWorkerId(1L);
        verify(whatsAppService).sendTaskAssignmentMessage(mockTask);
    }

    @Test
    public void testAssignTaskWithWhatsApp_MissingTaskName() throws Exception {
        // Arrange
        WhatsAppTaskAssignRequest request = new WhatsAppTaskAssignRequest();
        request.setWorkerId(1L);
        request.setDescription("Clean all windows");

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task name is required"));

        // Verify no service calls
        verify(taskManagerService, never()).assignTask(any(), any(), any(), any(), any());
        verify(whatsAppService, never()).sendTaskAssignmentMessage(any());
    }

    @Test
    public void testAssignTaskWithWhatsApp_MissingWorkerId() throws Exception {
        // Arrange
        WhatsAppTaskAssignRequest request = new WhatsAppTaskAssignRequest();
        request.setTaskName("Clean greenhouse");
        request.setDescription("Clean all windows");

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Worker ID is required"));

        // Verify no service calls
        verify(taskManagerService, never()).assignTask(any(), any(), any(), any(), any());
        verify(whatsAppService, never()).sendTaskAssignmentMessage(any());
    }

    @Test
    public void testAssignTaskWithWhatsApp_ServiceException() throws Exception {
        // Arrange
        WhatsAppTaskAssignRequest request = new WhatsAppTaskAssignRequest();
        request.setTaskName("Clean greenhouse");
        request.setWorkerId(1L);
        request.setDescription("Clean all windows");

        doThrow(new RuntimeException("Database error"))
                .when(taskManagerService).assignTask(anyString(), anyString(), anyLong(), anyString(), any());

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed to assign task")));

        // Verify service was called but WhatsApp was not
        verify(taskManagerService).assignTask("Clean greenhouse", "Clean all windows", 1L, null, null);
        verify(whatsAppService, never()).sendTaskAssignmentMessage(any());
    }

    @Test
    public void testAssignTaskWithWhatsApp_NoTasksFound() throws Exception {
        // Arrange
        WhatsAppTaskAssignRequest request = new WhatsAppTaskAssignRequest();
        request.setTaskName("Clean greenhouse");
        request.setWorkerId(1L);
        request.setDescription("Clean all windows");

        when(taskManagerService.getTasksByWorkerId(1L)).thenReturn(Collections.emptyList());
        doNothing().when(taskManagerService).assignTask(anyString(), anyString(), anyLong(), anyString(), any());

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task assigned successfully and WhatsApp notification sent"));

        // Verify task was assigned but no WhatsApp sent
        verify(taskManagerService).assignTask("Clean greenhouse", "Clean all windows", 1L, null, null);
        verify(taskManagerService).getTasksByWorkerId(1L);
        verify(whatsAppService, never()).sendTaskAssignmentMessage(any());
    }
}
