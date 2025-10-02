package com.nitroxen.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for OTP request")
public class OtpResponse {

    @Schema(description = "Status of the OTP request", example = "pending")
    private String status;

    @Schema(description = "Message describing the result", example = "OTP sent successfully via WhatsApp")
    private String message;

    @Schema(description = "Phone number (masked for security)", example = "+91****7890")
    private String phoneNumber;

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;
}
