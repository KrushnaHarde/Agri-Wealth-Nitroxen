package com.nitroxen.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new reservoir")
public class ReservoirRequest {

    @NotBlank(message = "Reservoir name is required")
    @Schema(description = "Name of the reservoir", example = "Main Tank")
    private String name;

    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    @Schema(description = "Capacity of the reservoir in liters", example = "10000.0")
    private Double capacity;

    @Schema(description = "Source of water for the reservoir", example = "Municipal")
    private String waterSource;

    @Schema(description = "Water treatment methods used", example = "UV filtration, Reverse Osmosis")
    private String waterTreatment;
}
