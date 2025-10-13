package com.nitroxen.demo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nitroxen.demo.dto.request.WhatsAppTaskAssignRequest;
import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.entity.Worker;
import com.nitroxen.demo.repository.TaskRepository;
import com.nitroxen.demo.repository.WorkerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class WhatsAppTaskManagerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Worker testWorker;

    @BeforeEach
    void setUp() {
        // Clean up database
        taskRepository.deleteAll();
        workerRepository.deleteAll();

        // Create test worker
        testWorker = new Worker();
        testWorker.setName("John Doe");
        testWorker.setEmail("john.doe@example.com");
        testWorker.setPhoneNumber("+1234567890");
        testWorker.setStatus(Worker.WorkerStatus.ACTIVE);
        testWorker = workerRepository.save(testWorker);
    }

    @Test
    public void testCompleteWhatsAppWorkflow() throws Exception {
        // Step 1: Assign task via WhatsApp API
        WhatsAppTaskAssignRequest request = new WhatsAppTaskAssignRequest();
        request.setTaskName("Clean greenhouse");
        request.setWorkerId(testWorker.getId());
        request.setDescription("Clean all windows and check irrigation");
        request.setDeadline("2025-10-15");

        // This will fail due to missing Twilio credentials in test, but will create the task
        mockMvc.perform(post("/api/manager/task-manager/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()); // Expected due to Twilio

        // Verify task was created in database
        List<Task> tasks = taskRepository.findByWorkerId(testWorker.getId());
        assertEquals(1, tasks.size());

        Task createdTask = tasks.get(0);
        assertEquals("Clean greenhouse", createdTask.getTitle());
        assertEquals(testWorker.getId(), createdTask.getWorkerId());
        assertEquals(Task.TaskStatus.ASSIGNED, createdTask.getStatus());

        // Step 2: Simulate WhatsApp webhook for task completion
        mockMvc.perform(post("/whatsapp/webhook")
                .param("From", "whatsapp:" + testWorker.getPhoneNumber())
                .param("Body", "task completed")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Message processed successfully"));

        // Verify task status was updated
        Task updatedTask = taskRepository.findById(createdTask.getId()).orElse(null);
        assertNotNull(updatedTask);
        assertEquals(Task.TaskStatus.COMPLETED, updatedTask.getStatus());
        assertNotNull(updatedTask.getCompletedAt());
    }

    @Test
    public void testWebhookMediaHandling() throws Exception {
        // Create a task first
        Task task = new Task();
        task.setTitle("Test Task");
        task.setWorkerId(testWorker.getId());
        task.setStatus(Task.TaskStatus.ASSIGNED);
        task = taskRepository.save(task);

        // Simulate WhatsApp media webhook
        mockMvc.perform(post("/whatsapp/webhook")
                .param("From", "whatsapp:" + testWorker.getPhoneNumber())
                .param("MessageType", "image")
                .param("MediaUrl0", "https://api.twilio.com/media/test123")
                .param("AccountSid", "AC123456")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Message processed successfully"));

        // Note: Media download will fail in test environment due to invalid URL
        // But the webhook processing logic is tested
    }

    @Test
    public void testWebhookWithUnknownWorker() throws Exception {
        // Simulate webhook from unknown phone number
        mockMvc.perform(post("/whatsapp/webhook")
                .param("From", "whatsapp:+9999999999")
                .param("Body", "completed")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Worker not found"));
    }
}
