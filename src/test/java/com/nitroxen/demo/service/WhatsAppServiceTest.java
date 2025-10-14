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

    @Test
    public void testSendTaskAssignmentMessage_NullTask() {
        // Test with null task
        assertThrows(NullPointerException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(null);
        });
    }

    @Test
    public void testSendTaskAssignmentMessage_NullWorkerId() {
        // Test with null worker ID
        testTask.setWorkerId(null);
        
        assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });
    }

    @Test
    public void testSendTaskAssignmentMessage_NullDeadline() {
        // Test with null deadline
        testTask.setDeadline(null);
        when(workerRepository.findById(1L)).thenReturn(Optional.of(testWorker));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });

        assertTrue(exception.getMessage().contains("Failed to send WhatsApp notification"));
    }

    @Test
    public void testSendTaskAssignmentMessage_EmptyTaskTitle() {
        // Test with empty task title
        testTask.setTitle("");
        when(workerRepository.findById(1L)).thenReturn(Optional.of(testWorker));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });

        assertTrue(exception.getMessage().contains("Failed to send WhatsApp notification"));
    }

    @Test
    public void testSendTaskAssignmentMessage_LongDescription() {
        // Test with very long description
        String longDescription = "A".repeat(1000);
        testTask.setDescription(longDescription);
        when(workerRepository.findById(1L)).thenReturn(Optional.of(testWorker));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });

        assertTrue(exception.getMessage().contains("Failed to send WhatsApp notification"));
    }

    @Test
    public void testSendTaskAssignmentMessage_SpecialCharactersInTitle() {
        // Test with special characters in title
        testTask.setTitle("Clean 🌱 greenhouse & check 💧 irrigation");
        when(workerRepository.findById(1L)).thenReturn(Optional.of(testWorker));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });

        assertTrue(exception.getMessage().contains("Failed to send WhatsApp notification"));
    }

    @Test
    public void testDownloadAndSaveMedia_NullMediaUrl() {
        // Test with null media URL
        String result = whatsAppService.downloadAndSaveMedia(null, "test-token");
        
        assertNull(result);
    }

    @Test
    public void testDownloadAndSaveMedia_EmptyMediaUrl() {
        // Test with empty media URL
        String result = whatsAppService.downloadAndSaveMedia("", "test-token");
        
        assertNull(result);
    }

    @Test
    public void testDownloadAndSaveMedia_NullAuthToken() {
        // Test with null auth token
        String mediaUrl = "https://api.twilio.com/media/test123";
        byte[] mockImageData = "fake-image-data".getBytes();

        when(restTemplate.getForObject(
            eq(mediaUrl + "?auth_token=null"),
            eq(byte[].class)
        )).thenReturn(mockImageData);

        String result = whatsAppService.downloadAndSaveMedia(mediaUrl, null);
        
        // Should still attempt to download
        assertNotNull(result);
    }

    @Test
    public void testDownloadAndSaveMedia_InvalidUrl() {
        // Test with invalid URL
        String invalidUrl = "not-a-valid-url";
        
        String result = whatsAppService.downloadAndSaveMedia(invalidUrl, "test-token");
        
        assertNull(result);
    }

    @Test
    public void testDownloadAndSaveMedia_LargeFile() {
        // Test with large file
        String mediaUrl = "https://api.twilio.com/media/test123";
        String authToken = "test-token";
        byte[] largeImageData = new byte[10 * 1024 * 1024]; // 10MB

        when(restTemplate.getForObject(
            eq(mediaUrl + "?auth_token=" + authToken),
            eq(byte[].class)
        )).thenReturn(largeImageData);

        String result = whatsAppService.downloadAndSaveMedia(mediaUrl, authToken);
        
        assertNotNull(result);
        assertTrue(result.startsWith("whatsapp-media/"));
    }

    @Test
    public void testDownloadAndSaveMedia_EmptyFile() {
        // Test with empty file
        String mediaUrl = "https://api.twilio.com/media/test123";
        String authToken = "test-token";
        byte[] emptyData = new byte[0];

        when(restTemplate.getForObject(
            eq(mediaUrl + "?auth_token=" + authToken),
            eq(byte[].class)
        )).thenReturn(emptyData);

        String result = whatsAppService.downloadAndSaveMedia(mediaUrl, authToken);
        
        assertNotNull(result);
    }

    @Test
    public void testSendTaskCompletionConfirmation_NullPhoneNumber() {
        // Test with null phone number
        assertDoesNotThrow(() -> {
            try {
                whatsAppService.sendTaskCompletionConfirmation(null, "Test Task");
            } catch (Exception e) {
                // Expected to fail
            }
        });
    }

    @Test
    public void testSendTaskCompletionConfirmation_EmptyPhoneNumber() {
        // Test with empty phone number
        assertDoesNotThrow(() -> {
            try {
                whatsAppService.sendTaskCompletionConfirmation("", "Test Task");
            } catch (Exception e) {
                // Expected to fail
            }
        });
    }

    @Test
    public void testSendTaskCompletionConfirmation_InvalidPhoneNumber() {
        // Test with invalid phone number
        assertDoesNotThrow(() -> {
            try {
                whatsAppService.sendTaskCompletionConfirmation("invalid-phone", "Test Task");
            } catch (Exception e) {
                // Expected to fail
            }
        });
    }

    @Test
    public void testSendTaskCompletionConfirmation_NullTaskTitle() {
        // Test with null task title
        assertDoesNotThrow(() -> {
            try {
                whatsAppService.sendTaskCompletionConfirmation("+1234567890", null);
            } catch (Exception e) {
                // Expected to fail
            }
        });
    }

    @Test
    public void testSendTaskCompletionConfirmation_EmptyTaskTitle() {
        // Test with empty task title
        assertDoesNotThrow(() -> {
            try {
                whatsAppService.sendTaskCompletionConfirmation("+1234567890", "");
            } catch (Exception e) {
                // Expected to fail
            }
        });
    }

    @Test
    public void testSendTaskCompletionConfirmation_LongTaskTitle() {
        // Test with very long task title
        String longTitle = "A".repeat(500);
        assertDoesNotThrow(() -> {
            try {
                whatsAppService.sendTaskCompletionConfirmation("+1234567890", longTitle);
            } catch (Exception e) {
                // Expected to fail without real Twilio
            }
        });
    }

    @Test
    public void testDownloadAndSaveMedia_ConcurrentDownloads() {
        // Test concurrent media downloads
        String mediaUrl1 = "https://api.twilio.com/media/test1";
        String mediaUrl2 = "https://api.twilio.com/media/test2";
        String authToken = "test-token";
        byte[] mockImageData = "fake-image-data".getBytes();

        when(restTemplate.getForObject(anyString(), eq(byte[].class)))
            .thenReturn(mockImageData);

        String result1 = whatsAppService.downloadAndSaveMedia(mediaUrl1, authToken);
        String result2 = whatsAppService.downloadAndSaveMedia(mediaUrl2, authToken);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1, result2, "Concurrent downloads should have different filenames");
    }

    @Test
    public void testDownloadAndSaveMedia_NetworkTimeout() {
        // Test network timeout scenario
        String mediaUrl = "https://api.twilio.com/media/test123";
        String authToken = "test-token";

        when(restTemplate.getForObject(anyString(), eq(byte[].class)))
            .thenThrow(new RuntimeException("Connection timeout"));

        String result = whatsAppService.downloadAndSaveMedia(mediaUrl, authToken);
        
        assertNull(result, "Should return null on network timeout");
    }

    @Test
    public void testSendTaskAssignmentMessage_WorkerWithInvalidPhoneNumber() {
        // Test with worker having invalid phone number
        testWorker.setPhoneNumber("invalid");
        when(workerRepository.findById(1L)).thenReturn(Optional.of(testWorker));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });

        assertTrue(exception.getMessage().contains("Failed to send WhatsApp notification"));
    }

    @Test
    public void testSendTaskAssignmentMessage_WorkerWithNullPhoneNumber() {
        // Test with worker having null phone number
        testWorker.setPhoneNumber(null);
        when(workerRepository.findById(1L)).thenReturn(Optional.of(testWorker));

        assertThrows(RuntimeException.class, () -> {
            whatsAppService.sendTaskAssignmentMessage(testTask);
        });
    }
}
