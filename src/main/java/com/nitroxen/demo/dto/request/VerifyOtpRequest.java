package com.nitroxen.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to verify OTP and reset password")
public class VerifyOtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format (e.g., +911234567890)")
    @Schema(description = "Phone number in E.164 format", example = "+911234567890")
    private String phoneNumber;

    @NotBlank(message = "OTP code is required")
    @Size(min = 4, max = 6, message = "OTP must be between 4 and 6 digits")
    @Pattern(regexp = "^\\d+$", message = "OTP must contain only digits")
    @Schema(description = "OTP code received via WhatsApp", example = "123456")
    private String otpCode;

    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Schema(description = "New password", example = "NewPassword123!")
    private String newPassword;
}
