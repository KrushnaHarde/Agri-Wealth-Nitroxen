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
@Schema(description = "Response containing zone details")
public class ZoneResponse {

    @Schema(description = "Zone ID", example = "1")
    private Long id;

    @Schema(description = "Name of the zone", example = "Zone 1")
    private String name;

    @Schema(description = "Type of growing system in the zone", example = "NFT")
    private String systemType;

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

    @Schema(description = "ID of the polyhouse this zone belongs to", example = "1")
    private Long polyhouseId;

    @Schema(description = "Name of the polyhouse this zone belongs to", example = "Polyhouse A")
    private String polyhouseName;

    @Schema(description = "ID of the water source reservoir", example = "1")
    private Long waterSourceId;

    @Schema(description = "Name of the water source reservoir", example = "Main Tank")
    private String waterSourceName;

    @Schema(description = "Creation timestamp", example = "2025-09-29T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-09-29T14:20:15")
    private LocalDateTime updatedAt;
}
