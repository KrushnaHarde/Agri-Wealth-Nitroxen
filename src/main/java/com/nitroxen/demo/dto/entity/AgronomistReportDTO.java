package com.nitroxen.demo.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgronomistReportDTO {
    private Long id;
    private Long agronomistId;
    private Long farmId;
    private String content;
    private String status;
}