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
}
