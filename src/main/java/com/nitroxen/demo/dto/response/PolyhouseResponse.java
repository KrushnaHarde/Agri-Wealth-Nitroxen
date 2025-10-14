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
@Schema(description = "Response containing polyhouse details")
public class PolyhouseResponse {

    @Schema(description = "Polyhouse ID", example = "1")
    private Long id;

    @Schema(description = "Name of the polyhouse", example = "Polyhouse A")
    private String name;

    @Schema(description = "Area of the polyhouse in square meters", example = "1000.0")
    private Double area;

    @Schema(description = "Type of the polyhouse", example = "Gothic")
    private String type;

    @Schema(description = "Specifications of the polyhouse", example = "6m height, galvanized steel structure")
    private String specifications;

    @Schema(description = "Equipment in the polyhouse", example = "Climate control, irrigation system")
    private String equipment;

    @Schema(description = "Growing method used in the polyhouse", example = "Hydroponic")
    private String growingType;

    @Schema(description = "ID of the farm this polyhouse belongs to", example = "1")
    private Long farmId;

    @Schema(description = "Name of the farm this polyhouse belongs to", example = "Green Valley Farm")
    private String farmName;

    @Schema(description = "Number of zones in this polyhouse", example = "3")
    private Integer zoneCount;

    @Schema(description = "Creation timestamp", example = "2025-09-29T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-09-29T14:20:15")
    private LocalDateTime updatedAt;
}
