package com.nitroxen.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "agronomist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agronomist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    private String specialization;

    @ManyToMany
    @JoinTable(
        name = "agronomist_farm",
        joinColumns = @JoinColumn(name = "agronomist_id"),
        inverseJoinColumns = @JoinColumn(name = "farm_id")
    )
    @Builder.Default
    private Set<Farm> farms = new HashSet<>();
}