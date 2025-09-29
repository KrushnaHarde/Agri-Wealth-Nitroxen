package com.nitroxen.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "zones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String systemType; // NFT, DWC, Drip, Ebb and Flow, etc.

    @Column(nullable = false)
    private String cropType; // Vegetable, fruit, herb, etc.

    private String cropVariety; // Specific crop variety

    private String plantingConfiguration; // Spacing, density, etc.

    private String irrigationSetup; // Details about irrigation

    private String dosingSystem; // Nutrient dosing details

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polyhouse_id", nullable = false)
    private Polyhouse polyhouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservoir_id")
    private Reservoir waterSource;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
