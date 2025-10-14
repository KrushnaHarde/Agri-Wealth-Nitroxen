package com.nitroxen.demo.service;

import com.nitroxen.demo.config.TwilioConfig;
import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.entity.Worker;
import com.nitroxen.demo.repository.WorkerRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class WhatsAppService {

    private final TwilioConfig twilioConfig;
    private final WorkerRepository workerRepository;
    private final RestTemplate restTemplate;

    public WhatsAppService(TwilioConfig twilioConfig, WorkerRepository workerRepository) {
        this.twilioConfig = twilioConfig;
        this.workerRepository = workerRepository;
        this.restTemplate = new RestTemplate();
    }

    public void sendTaskAssignmentMessage(Task task) {
        try {
            // Get worker phone number
            Worker worker = workerRepository.findById(task.getWorkerId())
                    .orElseThrow(() -> new RuntimeException("Worker not found with id: " + task.getWorkerId()));

            String messageBody = String.format(
                "🔔 *New Task Assigned*\n\n" +
                "📋 *Task:* %s\n" +
                "📝 *Description:* %s\n" +
                "📅 *Deadline:* %s\n\n" +
                "Reply with 'completed' when you finish this task.\n" +
                "You can also send a photo as proof of completion.",
                task.getTitle(),
                task.getDescription(),
                task.getDeadline() != null ? task.getDeadline() : "Not specified"
            );

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + worker.getPhoneNumber()),
                    new PhoneNumber("whatsapp:" + twilioConfig.getWhatsappNumber()),
                    messageBody
            ).create();

            System.out.println("WhatsApp message sent successfully. SID: " + message.getSid());

        } catch (Exception e) {
            System.err.println("Failed to send WhatsApp message: " + e.getMessage());
            throw new RuntimeException("Failed to send WhatsApp notification", e);
        }
    }

    public String downloadAndSaveMedia(String mediaUrl, String authToken) {
        try {
            // Create download directory
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path uploadPath = Paths.get("uploads", "whatsapp-media", timestamp);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String uniqueFilename = UUID.randomUUID().toString() + "_" + System.currentTimeMillis() + ".jpg";
            Path filePath = uploadPath.resolve(uniqueFilename);

            // Download media with authentication
            byte[] mediaData = restTemplate.getForObject(
                mediaUrl + "?auth_token=" + authToken,
                byte[].class
            );

            if (mediaData != null) {
                Files.write(filePath, mediaData);
                // Return relative path for database storage
                return "whatsapp-media/" + timestamp + "/" + uniqueFilename;
            }

            return null;
        } catch (Exception e) {
            System.err.println("Failed to download media: " + e.getMessage());
            return null;
        }
    }

    public void sendTaskCompletionConfirmation(String workerPhoneNumber, String taskTitle) {
        try {
            String messageBody = String.format(
                "✅ *Task Completed Successfully!*\n\n" +
                "📋 *Task:* %s\n" +
                "🎉 Thank you for completing this task!\n\n" +
                "Your completion has been recorded in the system.",
                taskTitle
            );

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + workerPhoneNumber),
                    new PhoneNumber("whatsapp:" + twilioConfig.getWhatsappNumber()),
                    messageBody
            ).create();

            System.out.println("Completion confirmation sent. SID: " + message.getSid());

        } catch (Exception e) {
            System.err.println("Failed to send completion confirmation: " + e.getMessage());
        }
    }
}
