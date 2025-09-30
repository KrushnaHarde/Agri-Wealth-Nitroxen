package com.nitroxen.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "agronomist_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgronomistReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agronomist_id", nullable = false)
    private Agronomist agronomist;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private String status = "Pending";

    @PrePersist
    @PreUpdate
    private void validateStatus() {
        if (status != null && !status.equals("Pending") && !status.equals("Completed")) {
            throw new IllegalArgumentException("Status must be either 'Pending' or 'Completed'");
        }
    }
}