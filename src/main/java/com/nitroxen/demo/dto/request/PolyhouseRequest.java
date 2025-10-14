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
@Schema(description = "Request payload for creating a new polyhouse")
public class PolyhouseRequest {

    @NotBlank(message = "Polyhouse name is required")
    @Schema(description = "Name of the polyhouse", example = "Polyhouse A")
    private String name;

    @NotNull(message = "Area is required")
    @Positive(message = "Area must be positive")
    @Schema(description = "Area of the polyhouse in square meters", example = "1000.0")
    private Double area;

    @NotBlank(message = "Polyhouse type is required")
    @Schema(description = "Type of the polyhouse", example = "Gothic")
    private String type;

    @Schema(description = "Specifications of the polyhouse", example = "6m height, galvanized steel structure")
    private String specifications;

    @Schema(description = "Equipment in the polyhouse", example = "Climate control, irrigation system")
    private String equipment;

    @NotBlank(message = "Growing type is required")
    @Schema(description = "Growing method used in the polyhouse", example = "Hydroponic")
    private String growingType;
}
