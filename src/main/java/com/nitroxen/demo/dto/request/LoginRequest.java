package com.nitroxen.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class LoginRequest {

    @NotBlank(message = "Phone number is required")
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "password123")
    private String password;
}
