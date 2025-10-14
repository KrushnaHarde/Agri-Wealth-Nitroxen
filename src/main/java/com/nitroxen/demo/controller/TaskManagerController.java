package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.AssignTaskRequest;
import com.nitroxen.demo.dto.request.SubmitReportRequest;
import com.nitroxen.demo.dto.request.WhatsAppTaskAssignRequest;
import com.nitroxen.demo.entity.Report;
import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.service.TaskManagerService;
import com.nitroxen.demo.service.WhatsAppService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/manager/task-manager")
public class TaskManagerController {

    private final TaskManagerService taskManagerService;
    private final WhatsAppService whatsAppService;

    public TaskManagerController(TaskManagerService taskManagerService, WhatsAppService whatsAppService) {
        this.taskManagerService = taskManagerService;
        this.whatsAppService = whatsAppService;
    }

    // NEW: WhatsApp Task Assignment Route
    @PostMapping("/assign")
    public ResponseEntity<String> assignTaskWithWhatsApp(@RequestBody WhatsAppTaskAssignRequest request) {
        try {
            // Basic validation
            if (request.getTaskName() == null || request.getTaskName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Task name is required");
            }
            if (request.getWorkerId() == null) {
                return ResponseEntity.badRequest().body("Worker ID is required");
            }

            // Create task using existing service
            taskManagerService.assignTask(
                request.getTaskName(),
                request.getDescription(),
                request.getWorkerId(),
                request.getDeadline(),
                null
            );

            // Get the created task to send WhatsApp notification
            List<Task> workerTasks = taskManagerService.getTasksByWorkerId(request.getWorkerId());
            if (!workerTasks.isEmpty()) {
                Task latestTask = workerTasks.get(workerTasks.size() - 1); // Get most recent
                whatsAppService.sendTaskAssignmentMessage(latestTask);
            }

            return ResponseEntity.ok("Task assigned successfully and WhatsApp notification sent");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to assign task: " + e.getMessage());
        }
    }

    // Existing JSON endpoint for task assignment (without WhatsApp notification)
    @PostMapping("/assign-simple")
    public ResponseEntity<String> assignTaskSimple(@RequestBody AssignTaskRequest request) {
        try {
            // Basic validation
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Title is required");
            }
            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Description is required");
            }
            if (request.getWorkerId() == null) {
                return ResponseEntity.badRequest().body("Worker ID is required");
            }

            taskManagerService.assignTask(request.getTitle(), request.getDescription(),
                    request.getWorkerId(), request.getDeadline(), null);
            return ResponseEntity.ok("Task assigned successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to assign task: " + e.getMessage());
        }
    }

    // Multipart endpoint for task assignment (with file)
    @PostMapping("/assign-with-file")
    public ResponseEntity<String> assignTaskWithFile(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Long workerId,
            @RequestParam(required = false) String deadline,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // Basic validation
            if (title == null || title.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Title is required");
            }
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Description is required");
            }
            if (workerId == null) {
                return ResponseEntity.badRequest().body("Worker ID is required");
            }

            taskManagerService.assignTask(title, description, workerId, deadline, file);
            return ResponseEntity.ok("Task assigned successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to assign task: " + e.getMessage());
        }
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<TaskWithDownloadUrl>> getTasks(@PathVariable Long workerId, HttpServletRequest request) {
        try {
            List<Task> tasks = taskManagerService.getTasksByWorkerId(workerId);
            String baseUrl = getBaseUrl(request);

            List<TaskWithDownloadUrl> tasksWithUrls = tasks.stream()
                    .map(task -> new TaskWithDownloadUrl(task,
                            task.getFileUrl() != null ? taskManagerService.generateDownloadUrl(task.getFileUrl(), baseUrl) : null))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(tasksWithUrls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // JSON endpoint for report submission (without file)
    @PostMapping("/report")
    public ResponseEntity<String> submitReport(@RequestBody SubmitReportRequest request) {
        try {
            // Basic validation
            if (request.getTaskId() == null) {
                return ResponseEntity.badRequest().body("Task ID is required");
            }
            if (request.getReportText() == null || request.getReportText().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Report text is required");
            }

            taskManagerService.submitReport(request.getTaskId(), request.getReportText(), null);
            return ResponseEntity.ok("Report submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to submit report: " + e.getMessage());
        }
    }

    // Multipart endpoint for report submission (with file)
    @PostMapping("/report-with-file")
    public ResponseEntity<String> submitReportWithFile(
            @RequestParam Long taskId,
            @RequestParam String reportText,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            // Basic validation
            if (taskId == null) {
                return ResponseEntity.badRequest().body("Task ID is required");
            }
            if (reportText == null || reportText.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Report text is required");
            }

            taskManagerService.submitReport(taskId, reportText, file);
            return ResponseEntity.ok("Report submitted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to submit report: " + e.getMessage());
        }
    }

    @GetMapping("/report/{taskId}")
    public ResponseEntity<List<Report>> getReports(@PathVariable Long taskId) {
        try {
            List<Report> reports = taskManagerService.getReportsByTaskId(taskId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        return scheme + "://" + serverName + (serverPort != 80 && serverPort != 443 ? ":" + serverPort : "") + contextPath;
    }

    // DTO class for task with download URL
    public static class TaskWithDownloadUrl {
        private Task task;
        private String downloadUrl;

        public TaskWithDownloadUrl(Task task, String downloadUrl) {
            this.task = task;
            this.downloadUrl = downloadUrl;
        }

        // Getters
        public Task getTask() { return task; }
        public String getDownloadUrl() { return downloadUrl; }

        // Delegate getters for easier JSON serialization
        public Long getId() { return task.getId(); }
        public String getTitle() { return task.getTitle(); }
        public String getDescription() { return task.getDescription(); }
        public Long getWorkerId() { return task.getWorkerId(); }
        public String getDeadline() { return task.getDeadline(); }
        public String getCreatedBy() { return task.getCreatedBy(); }
        public String getFileUrl() { return task.getFileUrl(); }
        public Object getCreatedAt() { return task.getCreatedAt(); }
    }
}
