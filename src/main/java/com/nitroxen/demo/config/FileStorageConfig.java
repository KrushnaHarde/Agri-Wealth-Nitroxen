package com.nitroxen.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }

            // Create subdirectories for different file types
            Path tasksDir = uploadPath.resolve("tasks");
            Path reportsDir = uploadPath.resolve("reports");

            if (!Files.exists(tasksDir)) {
                Files.createDirectories(tasksDir);
                System.out.println("Created tasks directory: " + tasksDir.toAbsolutePath());
            }

            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
                System.out.println("Created reports directory: " + reportsDir.toAbsolutePath());
            }

        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories!", e);
        }
    }
}
