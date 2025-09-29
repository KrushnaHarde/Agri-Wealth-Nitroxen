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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polyhouses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Polyhouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double area; // in square meters

    @Column(nullable = false)
    private String type; // e.g., Gothic, Quonset, Venlo, etc.

    private String specifications; // Structural details, materials, etc.

    private String equipment; // Installed equipment description

    @Column(nullable = false)
    private String growingType; // Soil, hydroponic, aeroponic, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @OneToMany(mappedBy = "polyhouse", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Zone> zones = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Check if polyhouse has reached maximum number of zones (4)
    public boolean hasMaxZones() {
        return zones.size() >= 4;
    }
}
