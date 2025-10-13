package com.nitroxen.demo.controller;

import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.entity.Worker;
import com.nitroxen.demo.repository.TaskRepository;
import com.nitroxen.demo.repository.WorkerRepository;
import com.nitroxen.demo.service.WhatsAppService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(WhatsAppWebhookController.class)
public class WhatsAppWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private WorkerRepository workerRepository;

    @MockBean
    private WhatsAppService whatsAppService;

    @Test
    public void testReceiveWhatsAppMessage_TaskCompletion_Success() throws Exception {
        // Arrange
        Worker worker = new Worker();
        worker.setId(1L);
        worker.setName("John Doe");
        worker.setPhoneNumber("+1234567890");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Clean greenhouse");
        task.setWorkerId(1L);
        task.setStatus(Task.TaskStatus.ASSIGNED);

        when(workerRepository.findByPhoneNumber("+1234567890")).thenReturn(Optional.of(worker));
        when(taskRepository.findByWorkerIdAndStatus(1L, Task.TaskStatus.ASSIGNED))
                .thenReturn(Arrays.asList(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        doNothing().when(whatsAppService).sendTaskCompletionConfirmation(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/whatsapp/webhook")
                .param("From", "whatsapp:+1234567890")
                .param("Body", "completed")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Message processed successfully"));

        // Verify task was completed
        verify(taskRepository).save(argThat(savedTask ->
            savedTask.getStatus() == Task.TaskStatus.COMPLETED));
        verify(whatsAppService).sendTaskCompletionConfirmation("+1234567890", "Clean greenhouse");
    }

    @Test
    public void testReceiveWhatsAppMessage_MediaMessage_Success() throws Exception {
        // Arrange
        Worker worker = new Worker();
        worker.setId(1L);
        worker.setName("John Doe");
        worker.setPhoneNumber("+1234567890");

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Clean greenhouse");
        task.setWorkerId(1L);
        task.setStatus(Task.TaskStatus.ASSIGNED);

        when(workerRepository.findByPhoneNumber("+1234567890")).thenReturn(Optional.of(worker));
        when(taskRepository.findByWorkerIdAndStatus(1L, Task.TaskStatus.ASSIGNED))
                .thenReturn(Arrays.asList(task));
        when(whatsAppService.downloadAndSaveMedia(anyString(), anyString()))
                .thenReturn("whatsapp-media/2025/10/08/test-image.jpg");
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        doNothing().when(whatsAppService).sendTaskCompletionConfirmation(anyString(), anyString());

        // Act & Assert
        mockMvc.perform(post("/whatsapp/webhook")
                .param("From", "whatsapp:+1234567890")
                .param("MessageType", "image")
                .param("MediaUrl0", "https://api.twilio.com/media/test123")
                .param("AccountSid", "AC123456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Message processed successfully"));

        // Verify task was completed with image
        verify(whatsAppService).downloadAndSaveMedia("https://api.twilio.com/media/test123", "AC123456");
        verify(taskRepository).save(argThat(savedTask ->
            savedTask.getStatus() == Task.TaskStatus.COMPLETED &&
            savedTask.getCompletionImageUrl().equals("whatsapp-media/2025/10/08/test-image.jpg")));
        verify(whatsAppService).sendTaskCompletionConfirmation("+1234567890", "Clean greenhouse");
    }

    @Test
    public void testReceiveWhatsAppMessage_WorkerNotFound() throws Exception {
        // Arrange
        when(workerRepository.findByPhoneNumber("+1234567890")).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(post("/whatsapp/webhook")
                .param("From", "whatsapp:+1234567890")
                .param("Body", "completed")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Worker not found"));

        // Verify no task operations
        verify(taskRepository, never()).findByWorkerIdAndStatus(any(), any());
        verify(taskRepository, never()).save(any());
        verify(whatsAppService, never()).sendTaskCompletionConfirmation(any(), any());
    }

    @Test
    public void testReceiveWhatsAppMessage_NoTasksFound() throws Exception {
        // Arrange
        Worker worker = new Worker();
        worker.setId(1L);
        worker.setName("John Doe");
        worker.setPhoneNumber("+1234567890");

        when(workerRepository.findByPhoneNumber("+1234567890")).thenReturn(Optional.of(worker));
        when(taskRepository.findByWorkerIdAndStatus(1L, Task.TaskStatus.ASSIGNED))
                .thenReturn(Collections.emptyList());
        when(taskRepository.findByWorkerIdAndStatus(1L, Task.TaskStatus.IN_PROGRESS))
                .thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/whatsapp/webhook")
                .param("From", "whatsapp:+1234567890")
                .param("Body", "completed")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Message processed successfully"));

        // Verify no task completion
        verify(taskRepository, never()).save(any());
        verify(whatsAppService, never()).sendTaskCompletionConfirmation(any(), any());
    }

    @Test
    public void testReceiveWhatsAppMessage_InvalidPhoneNumber() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/whatsapp/webhook")
                .param("Body", "completed")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid phone number"));

        // Verify no operations
        verify(workerRepository, never()).findByPhoneNumber(any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    public void testWebhookVerification() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/whatsapp/webhook")
                .param("hub.challenge", "test-challenge-123"))
                .andExpect(status().isOk())
                .andExpect(content().string("test-challenge-123"));
    }

    @Test
    public void testWebhookVerification_NoChallenge() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/whatsapp/webhook"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
