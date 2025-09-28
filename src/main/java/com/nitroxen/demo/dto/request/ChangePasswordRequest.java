package com.nitroxen.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Change password request")
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password", example = "oldPassword123")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Schema(description = "New password", example = "newPassword123")
    private String newPassword;
}
