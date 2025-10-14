package com.nitroxen.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "workers")
@Data
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber; // In E.164 format (e.g., +919876543210)

    @Enumerated(EnumType.STRING)
    private WorkerStatus status = WorkerStatus.ACTIVE;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum WorkerStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}
