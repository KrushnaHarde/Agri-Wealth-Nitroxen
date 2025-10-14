package com.nitroxen.demo.service;

import com.nitroxen.demo.entity.Report;
import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.repository.ReportRepository;
import com.nitroxen.demo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class TaskManagerService {

    private final TaskRepository taskRepository;
    private final ReportRepository reportRepository;
    
    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;
    
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".pdf", ".doc", ".docx", ".txt"};

    public TaskManagerService(TaskRepository taskRepository, ReportRepository reportRepository) {
        this.taskRepository = taskRepository;
        this.reportRepository = reportRepository;
    }

    public void assignTask(String title, String description, Long workerId, String deadline, MultipartFile file) {
        try {
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setWorkerId(workerId);
            task.setDeadline(deadline);
            task.setCreatedBy("MANAGER"); // Set default creator
            task.setCreatedAt(LocalDateTime.now());

            if (file != null && !file.isEmpty()) {
                String fileUrl = saveFile(file, "tasks");
                task.setFileUrl(fileUrl);
            }

            taskRepository.save(task);
        } catch (Exception e) {
            throw new RuntimeException("Failed to assign task: " + e.getMessage(), e);
        }
    }

    public List<Task> getTasksByWorkerId(Long workerId) {
        return taskRepository.findByWorkerId(workerId);
    }

    public void submitReport(Long taskId, String reportText, MultipartFile file) {
        try {
            // Verify task exists
            Task task = taskRepository.findById(taskId)
                    .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

            Report report = new Report();
            report.setTaskId(taskId);
            report.setWorkerId(task.getWorkerId()); // Get workerId from the task
            report.setReportText(reportText);
            report.setSubmittedAt(LocalDateTime.now());

            if (file != null && !file.isEmpty()) {
                String fileUrl = saveFile(file, "reports");
                report.setFileUrl(fileUrl);
            }

            reportRepository.save(report);
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit report: " + e.getMessage(), e);
        }
    }

    public List<Report> getReportsByTaskId(Long taskId) {
        return reportRepository.findByTaskId(taskId);
    }

    private String saveFile(MultipartFile file, String subDirectory) throws IOException {
        // Validate file
        validateFile(file);
        
        // Create upload directory structure
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(uploadDir, subDirectory, timestamp);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "_" +
                               System.currentTimeMillis() + fileExtension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return relative path for storage in database
        return subDirectory + "/" + timestamp + "/" + uniqueFilename;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum limit of 10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("File name is invalid");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        boolean isAllowedExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (fileExtension.equals(allowedExt)) {
                isAllowedExtension = true;
                break;
            }
        }

        if (!isAllowedExtension) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: jpg, jpeg, png, pdf, doc, docx, txt");
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    // Method to get absolute file path for file serving
    public Path getFilePath(String relativePath) {
        return Paths.get(uploadDir).resolve(relativePath);
    }

    // Method to check if file exists
    public boolean fileExists(String relativePath) {
        Path filePath = getFilePath(relativePath);
        return Files.exists(filePath);
    }

    // Helper method to generate download URL
    public String generateDownloadUrl(String fileUrl, String baseUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        return baseUrl + "/api/files/download/" + fileUrl;
    }

    // Helper method to get file info
    public FileInfo getFileInfo(String relativePath) {
        if (relativePath == null || !fileExists(relativePath)) {
            return null;
        }

        Path filePath = getFilePath(relativePath);
        try {
            long size = Files.size(filePath);
            String fileName = filePath.getFileName().toString();
            String originalName = extractOriginalFileName(fileName);

            return new FileInfo(fileName, originalName, size, relativePath);
        } catch (IOException e) {
            return null;
        }
    }

    private String extractOriginalFileName(String uniqueFileName) {
        // Extract original name from UUID_timestamp_originalname.ext format
        int firstUnderscore = uniqueFileName.indexOf('_');
        int secondUnderscore = uniqueFileName.indexOf('_', firstUnderscore + 1);
        if (secondUnderscore != -1) {
            return uniqueFileName.substring(secondUnderscore + 1);
        }
        return uniqueFileName;
    }

    // Inner class for file information
    public static class FileInfo {
        private String fileName;
        private String originalName;
        private long size;
        private String relativePath;

        public FileInfo(String fileName, String originalName, long size, String relativePath) {
            this.fileName = fileName;
            this.originalName = originalName;
            this.size = size;
            this.relativePath = relativePath;
        }

        // Getters
        public String getFileName() { return fileName; }
        public String getOriginalName() { return originalName; }
        public long getSize() { return size; }
        public String getRelativePath() { return relativePath; }
    }
}
