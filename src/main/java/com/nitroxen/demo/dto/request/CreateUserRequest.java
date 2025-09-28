package com.nitroxen.demo.dto.request;

import com.nitroxen.demo.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Create user request")
public class CreateUserRequest {

    @NotBlank(message = "Name is required")
    @Schema(description = "User's full name", example = "John Doe")
    private String name;

    @Email(message = "Valid email is required")
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Schema(description = "User's phone number", example = "+1234567890")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "password123")
    private String password;

    @NotNull(message = "Role is required")
    @Schema(description = "User's role", example = "MANAGER")
    private Role role;
}
