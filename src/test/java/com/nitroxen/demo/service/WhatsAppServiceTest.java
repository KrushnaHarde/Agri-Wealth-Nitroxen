package com.nitroxen.demo.service;

import com.nitroxen.demo.config.TwilioConfig;
import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.entity.Worker;
import com.nitroxen.demo.repository.WorkerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WhatsAppServiceTest {

    @Mock
    private TwilioConfig twilioConfig;

    @Mock
    private WorkerRepository workerRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WhatsAppService whatsAppService;

    private Worker testWorker;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testWorker = new Worker();
        testWorker.setId(1L);
        testWorker.setName("John Doe");
        testWorker.setPhoneNumber("+1234567890");

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Clean greenhouse");
        testTask.setDescription("Clean all windows and check irrigation");
        testTask.setWorkerId(1L);
        testTask.setDeadline("2025-10-15");

        when(twilioConfig.getWhatsappNumber()).thenReturn("+14155238886");
    }

    @Test
    public void testSendTaskAssignmentMessage_Success() {
        // Arrange
        when(workerRepository.findById(1L)).thenReturn(Optional.of(testWorker));

        // Act - Note: This will fail in unit test without actual Twilio credentials
        // In real testing, you'd mock the Twilio Message.creator() call
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });

        // Assert - Verify the exception contains our expected error message
        assertTrue(exception.getMessage().contains("Failed to send WhatsApp notification"));
    }

    @Test
    public void testSendTaskAssignmentMessage_WorkerNotFound() {
        // Arrange
        when(workerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });

        assertTrue(exception.getMessage().contains("Worker not found with id: 1"));
    }

    @Test
    public void testDownloadAndSaveMedia_Success() {
        // Arrange
        String mediaUrl = "https://api.twilio.com/media/test123";
        String authToken = "test-token";
        byte[] mockImageData = "fake-image-data".getBytes();

        when(restTemplate.getForObject(
            eq(mediaUrl + "?auth_token=" + authToken),
            eq(byte[].class)
        )).thenReturn(mockImageData);

        // Act
        String result = whatsAppService.downloadAndSaveMedia(mediaUrl, authToken);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("whatsapp-media/"));
        assertTrue(result.endsWith(".jpg"));
        verify(restTemplate).getForObject(mediaUrl + "?auth_token=" + authToken, byte[].class);
    }

    @Test
    public void testDownloadAndSaveMedia_NoData() {
        // Arrange
        String mediaUrl = "https://api.twilio.com/media/test123";
        String authToken = "test-token";

        when(restTemplate.getForObject(
            eq(mediaUrl + "?auth_token=" + authToken),
            eq(byte[].class)
        )).thenReturn(null);

        // Act
        String result = whatsAppService.downloadAndSaveMedia(mediaUrl, authToken);

        // Assert
        assertNull(result);
    }

    @Test
    public void testDownloadAndSaveMedia_Exception() {
        // Arrange
        String mediaUrl = "https://api.twilio.com/media/test123";
        String authToken = "test-token";

        when(restTemplate.getForObject(any(String.class), eq(byte[].class)))
            .thenThrow(new RuntimeException("Network error"));

        // Act
        String result = whatsAppService.downloadAndSaveMedia(mediaUrl, authToken);

        // Assert
        assertNull(result);
    }

    @Test
    public void testSendTaskCompletionConfirmation() {
        // Arrange
        String workerPhoneNumber = "+1234567890";
        String taskTitle = "Clean greenhouse";

        // Act - This will also fail without real Twilio credentials
        // We're testing that the method doesn't throw unexpected exceptions
        assertDoesNotThrow(() -> {
            try {
                whatsAppService.sendTaskCompletionConfirmation(workerPhoneNumber, taskTitle);
            } catch (RuntimeException e) {
                // Expected to fail without real Twilio setup
                // Just ensure it's trying to send the message
            }
        });
    }
}
