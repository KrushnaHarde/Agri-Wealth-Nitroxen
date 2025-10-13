package com.nitroxen.demo.controller;

import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.entity.Worker;
import com.nitroxen.demo.repository.TaskRepository;
import com.nitroxen.demo.repository.WorkerRepository;
import com.nitroxen.demo.service.WhatsAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/whatsapp")
public class WhatsAppWebhookController {

    private final TaskRepository taskRepository;
    private final WorkerRepository workerRepository;
    private final WhatsAppService whatsAppService;

    public WhatsAppWebhookController(TaskRepository taskRepository,
                                   WorkerRepository workerRepository,
                                   WhatsAppService whatsAppService) {
        this.taskRepository = taskRepository;
        this.workerRepository = workerRepository;
        this.whatsAppService = whatsAppService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWhatsAppMessage(@RequestParam Map<String, String> payload) {
        try {
            System.out.println("Received WhatsApp webhook: " + payload);

            String from = payload.get("From"); // Format: whatsapp:+1234567890
            String body = payload.get("Body");
            String messageType = payload.get("MessageType");
            String mediaSid = payload.get("MediaSid0");
            String mediaUrl = payload.get("MediaUrl0");

            // Extract phone number (remove whatsapp: prefix)
            String phoneNumber = from != null ? from.replace("whatsapp:", "") : null;

            if (phoneNumber == null) {
                return ResponseEntity.badRequest().body("Invalid phone number");
            }

            // Find worker by phone number
            Optional<Worker> workerOpt = workerRepository.findByPhoneNumber(phoneNumber);
            if (workerOpt.isEmpty()) {
                System.out.println("Worker not found for phone: " + phoneNumber);
                return ResponseEntity.ok("Worker not found");
            }

            Worker worker = workerOpt.get();

            // Handle text message
            if (body != null && body.toLowerCase().contains("completed")) {
                handleTaskCompletion(worker, body);
            }

            // Handle media message (image)
            if ("image".equals(messageType) && mediaUrl != null) {
                handleMediaMessage(worker, mediaUrl, payload.get("AccountSid"));
            }

            return ResponseEntity.ok("Message processed successfully");

        } catch (Exception e) {
            System.err.println("Error processing WhatsApp webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error processing message");
        }
    }

    private void handleTaskCompletion(Worker worker, String messageBody) {
        try {
            // Find the most recent assigned task for this worker
            List<Task> assignedTasks = taskRepository.findByWorkerIdAndStatus(
                worker.getId(), Task.TaskStatus.ASSIGNED
            );

            if (assignedTasks.isEmpty()) {
                // Check for in-progress tasks
                assignedTasks = taskRepository.findByWorkerIdAndStatus(
                    worker.getId(), Task.TaskStatus.IN_PROGRESS
                );
            }

            if (!assignedTasks.isEmpty()) {
                // Complete the most recent task
                Task task = assignedTasks.get(0);
                task.setStatus(Task.TaskStatus.COMPLETED);
                taskRepository.save(task);

                System.out.println("Task completed: " + task.getTitle() + " by worker: " + worker.getName());

                // Send confirmation message
                whatsAppService.sendTaskCompletionConfirmation(
                    worker.getPhoneNumber(),
                    task.getTitle()
                );
            } else {
                System.out.println("No pending tasks found for worker: " + worker.getName());
            }

        } catch (Exception e) {
            System.err.println("Error handling task completion: " + e.getMessage());
        }
    }

    private void handleMediaMessage(Worker worker, String mediaUrl, String accountSid) {
        try {
            // Download and save the media
            String savedImagePath = whatsAppService.downloadAndSaveMedia(mediaUrl, accountSid);

            if (savedImagePath != null) {
                // Find the most recent task for this worker
                List<Task> recentTasks = taskRepository.findByWorkerIdAndStatus(
                    worker.getId(), Task.TaskStatus.ASSIGNED
                );

                if (recentTasks.isEmpty()) {
                    recentTasks = taskRepository.findByWorkerIdAndStatus(
                        worker.getId(), Task.TaskStatus.IN_PROGRESS
                    );
                }

                if (!recentTasks.isEmpty()) {
                    Task task = recentTasks.get(0);
                    task.setCompletionImageUrl(savedImagePath);
                    task.setStatus(Task.TaskStatus.COMPLETED);
                    taskRepository.save(task);

                    System.out.println("Task completed with image: " + task.getTitle());

                    // Send confirmation
                    whatsAppService.sendTaskCompletionConfirmation(
                        worker.getPhoneNumber(),
                        task.getTitle()
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("Error handling media message: " + e.getMessage());
        }
    }

    // Webhook verification endpoint for Twilio
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(@RequestParam Map<String, String> params) {
        // Return the challenge parameter for webhook verification
        String challenge = params.get("hub.challenge");
        return ResponseEntity.ok(challenge != null ? challenge : "OK");
    }
}
