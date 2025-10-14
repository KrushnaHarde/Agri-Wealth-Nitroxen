package com.nitroxen.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitroxen.demo.config.JwtAuthenticationFilter;
import com.nitroxen.demo.config.JwtService;
import com.nitroxen.demo.config.SecurityConfiguration;
import com.nitroxen.demo.dto.request.AssignTaskRequest;
import com.nitroxen.demo.dto.request.SubmitReportRequest;
import com.nitroxen.demo.service.TaskManagerService;
import com.nitroxen.demo.service.UserService;
import com.nitroxen.demo.service.WhatsAppService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = TaskManagerController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                              classes = {SecurityConfiguration.class, JwtAuthenticationFilter.class})
    })
@WithMockUser(username = "manager", roles = {"MANAGER"})
public class TaskManagerControllerTest {

    @Configuration
    @EnableWebSecurity
    static class SecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                    .anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskManagerService taskManagerService;

    @MockBean
    private WhatsAppService whatsAppService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAssignTaskSimple_Success() throws Exception {
        // Arrange
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setWorkerId(1L);
        request.setDeadline("2024-12-31");

        doNothing().when(taskManagerService).assignTask(anyString(), anyString(), anyLong(), anyString(), any());

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign-simple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Task assigned successfully"));

        verify(taskManagerService, times(1)).assignTask(
                eq("Test Task"), eq("Test Description"), eq(1L), eq("2024-12-31"), any());
    }

    @Test
    public void testAssignTaskSimple_MissingTitle() throws Exception {
        // Arrange
        AssignTaskRequest request = new AssignTaskRequest();
        request.setDescription("Test Description");
        request.setWorkerId(1L);
        request.setDeadline("2024-12-31");

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign-simple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Title is required"));

        verify(taskManagerService, never()).assignTask(any(), any(), any(), any(), any());
    }

    @Test
    public void testAssignTaskSimple_MissingDescription() throws Exception {
        // Arrange
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTitle("Test Task");
        request.setWorkerId(1L);
        request.setDeadline("2024-12-31");

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign-simple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Description is required"));

        verify(taskManagerService, never()).assignTask(any(), any(), any(), any(), any());
    }

    @Test
    public void testAssignTaskSimple_MissingWorkerId() throws Exception {
        // Arrange
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setDeadline("2024-12-31");

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign-simple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Worker ID is required"));

        verify(taskManagerService, never()).assignTask(any(), any(), any(), any(), any());
    }

    @Test
    public void testAssignTaskWithFile_Success() throws Exception {
        // Arrange
        doNothing().when(taskManagerService).assignTask(anyString(), anyString(), anyLong(), anyString(), any());

        // Act & Assert
        mockMvc.perform(multipart("/api/manager/task-manager/assign-with-file")
                .param("title", "Test Task")
                .param("description", "Test Description")
                .param("workerId", "1")
                .param("deadline", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task assigned successfully"));

        verify(taskManagerService, times(1)).assignTask(
                eq("Test Task"), eq("Test Description"), eq(1L), eq("2024-12-31"), any());
    }

    @Test
    public void testSubmitReport_Success() throws Exception {
        // Arrange
        SubmitReportRequest request = new SubmitReportRequest();
        request.setTaskId(1L);
        request.setReportText("Test Report");

        doNothing().when(taskManagerService).submitReport(anyLong(), anyString(), any());

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Report submitted successfully"));

        verify(taskManagerService, times(1)).submitReport(eq(1L), eq("Test Report"), any());
    }

    @Test
    public void testSubmitReport_MissingTaskId() throws Exception {
        // Arrange
        SubmitReportRequest request = new SubmitReportRequest();
        request.setReportText("Test Report");

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task ID is required"));

        verify(taskManagerService, never()).submitReport(any(), any(), any());
    }

    @Test
    public void testSubmitReport_MissingReportText() throws Exception {
        // Arrange
        SubmitReportRequest request = new SubmitReportRequest();
        request.setTaskId(1L);

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Report text is required"));

        verify(taskManagerService, never()).submitReport(any(), any(), any());
    }

    @Test
    public void testSubmitReportWithFile_Success() throws Exception {
        // Arrange
        doNothing().when(taskManagerService).submitReport(anyLong(), anyString(), any());

        // Act & Assert
        mockMvc.perform(multipart("/api/manager/task-manager/report-with-file")
                .param("taskId", "1")
                .param("reportText", "Test Report"))
                .andExpect(status().isOk())
                .andExpect(content().string("Report submitted successfully"));

        verify(taskManagerService, times(1)).submitReport(eq(1L), eq("Test Report"), any());
    }

    @Test
    public void testAssignTaskSimple_ServiceThrowsException() throws Exception {
        // Arrange
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTitle("Test Task");
        request.setDescription("Test Description");
        request.setWorkerId(1L);

        doThrow(new RuntimeException("Database error")).when(taskManagerService)
                .assignTask(anyString(), anyString(), anyLong(), anyString(), any());

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/assign-simple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed to assign task")))
                .andExpect(content().string(containsString("Database error")));
    }

    @Test
    public void testSubmitReport_ServiceThrowsException() throws Exception {
        // Arrange
        SubmitReportRequest request = new SubmitReportRequest();
        request.setTaskId(999L);
        request.setReportText("Test Report");

        doThrow(new RuntimeException("Task not found")).when(taskManagerService)
                .submitReport(anyLong(), anyString(), any());

        // Act & Assert
        mockMvc.perform(post("/api/manager/task-manager/report")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Failed to submit report")))
                .andExpect(content().string(containsString("Task not found")));
    }
}
