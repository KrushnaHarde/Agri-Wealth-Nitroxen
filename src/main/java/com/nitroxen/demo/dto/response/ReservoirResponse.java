package com.nitroxen.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing reservoir details")
public class ReservoirResponse {

    @Schema(description = "Reservoir ID", example = "1")
    private Long id;

    @Schema(description = "Name of the reservoir", example = "Main Tank")
    private String name;

    @Schema(description = "Capacity of the reservoir in liters", example = "10000.0")
    private Double capacity;

    @Schema(description = "Source of water for the reservoir", example = "Municipal")
    private String waterSource;

    @Schema(description = "Water treatment methods used", example = "UV filtration, Reverse Osmosis")
    private String waterTreatment;

    @Schema(description = "ID of the farm this reservoir belongs to", example = "1")
    private Long farmId;

    @Schema(description = "Name of the farm this reservoir belongs to", example = "Green Valley Farm")
    private String farmName;

    @Schema(description = "Number of zones this reservoir serves", example = "3")
    private Integer servingZonesCount;

    @Schema(description = "Creation timestamp", example = "2025-09-29T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-09-29T14:20:15")
    private LocalDateTime updatedAt;
}
