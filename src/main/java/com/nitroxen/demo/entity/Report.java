package com.nitroxen.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long taskId;

    private Long workerId;

    private String reportText;

    private String fileUrl;

    private LocalDateTime submittedAt = LocalDateTime.now();

//    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public Long getTaskId() {
//        return taskId;
//    }
//
//    public void setTaskId(Long taskId) {
//        this.taskId = taskId;
//
//
//    public Long getWorkerId() {
//        return workerId;
//    }
//
//    public void setWorkerId(Long workerId) {
//        this.workerId = workerId;
//    }
//
//    public String getReportText() {
//        return reportText;
//    }
//
//    public void setReportText(String reportText) {
//        this.reportText = reportText;
//    }
//
//    public String getFileUrl() {
//        return fileUrl;
//    }
//
//    public void setFileUrl(String fileUrl) {
//        this.fileUrl = fileUrl;
//    }
//
//    public LocalDateTime getSubmittedAt() {
//        return submittedAt;
//    }
//
//    public void setSubmittedAt(LocalDateTime submittedAt) {
//        this.submittedAt = submittedAt;
//    }
}
