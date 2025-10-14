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
@Schema(description = "Request payload for creating a new farm")
public class FarmRequest {

    @NotBlank(message = "Farm name is required")
    @Schema(description = "Name of the farm", example = "Green Valley Farm")
    private String name;

    @Schema(description = "Location of the farm", example = "123 Rural Road, Farmville")
    private String location;

    @NotNull(message = "Total area is required")
    @Positive(message = "Total area must be positive")
    @Schema(description = "Total area of the farm in square meters", example = "10000.0")
    private Double totalArea;

    @Schema(description = "Description of the farm", example = "Organic vegetable farm with modern technology")
    private String description;
}
