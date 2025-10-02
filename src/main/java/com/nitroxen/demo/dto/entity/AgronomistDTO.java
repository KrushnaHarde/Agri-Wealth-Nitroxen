package com.nitroxen.demo.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgronomistDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    
    @Builder.Default
    private Set<Long> farmIds = new HashSet<>();
}