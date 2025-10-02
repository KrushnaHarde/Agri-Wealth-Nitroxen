package com.nitroxen.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new zone")
public class ZoneRequest {

    @NotBlank(message = "Zone name is required")
    @Schema(description = "Name of the zone", example = "Zone 1")
    private String name;

    @NotBlank(message = "System type is required")
    @Schema(description = "Type of growing system in the zone", example = "NFT")
    private String systemType;

    @NotBlank(message = "Crop type is required")
    @Schema(description = "Type of crop grown in the zone", example = "Leafy Greens")
    private String cropType;

    @Schema(description = "Specific variety of crop", example = "Butterhead Lettuce")
    private String cropVariety;

    @Schema(description = "Configuration for planting", example = "15cm spacing, staggered pattern")
    private String plantingConfiguration;

    @Schema(description = "Details of irrigation setup", example = "Drip irrigation with timers")
    private String irrigationSetup;

    @Schema(description = "Details of nutrient dosing system", example = "Automated EC/pH control")
    private String dosingSystem;

    @Schema(description = "ID of the reservoir to use as water source", example = "1")
    private Long reservoirId;
}
