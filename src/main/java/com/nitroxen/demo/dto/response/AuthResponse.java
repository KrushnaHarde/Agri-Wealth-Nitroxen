package com.nitroxen.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication response")
public class AuthResponse {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "User ID")
    private Long userId;

    @Schema(description = "User name")
    private String name;

    @Schema(description = "User role")
    private String role;

    @Schema(description = "JWT token for backward compatibility", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration time in seconds", example = "3600")
    private Long expiresIn;

    @Schema(description = "User ID", example = "1")
    private Long userId;

}
