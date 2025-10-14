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
@Schema(description = "Response containing farm details")
public class FarmResponse {

    @Schema(description = "Farm ID", example = "1")
    private Long id;

    @Schema(description = "Name of the farm", example = "Green Valley Farm")
    private String name;

    @Schema(description = "Location of the farm", example = "123 Rural Road, Farmville")
    private String location;

    @Schema(description = "Total area of the farm in square meters", example = "10000.0")
    private Double totalArea;

    @Schema(description = "Area currently used by polyhouses in square meters", example = "4500.0")
    private Double usedArea;

    @Schema(description = "Remaining available area in square meters", example = "5500.0")
    private Double remainingArea;

    @Schema(description = "Description of the farm", example = "Organic vegetable farm with modern technology")
    private String description;

    @Schema(description = "Owner ID", example = "1")
    private Long ownerId;

    @Schema(description = "Owner name", example = "John Doe")
    private String ownerName;

    @Schema(description = "Creation timestamp", example = "2025-09-29T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2025-09-29T14:20:15")
    private LocalDateTime updatedAt;
}
