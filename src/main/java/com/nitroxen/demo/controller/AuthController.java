package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.ChangePasswordRequest;
import com.nitroxen.demo.dto.request.ForgotPasswordRequest;
import com.nitroxen.demo.dto.request.LoginRequest;
import com.nitroxen.demo.dto.request.VerifyOtpRequest;
import com.nitroxen.demo.dto.response.AuthResponse;
import com.nitroxen.demo.dto.response.OtpResponse;
import com.nitroxen.demo.dto.response.UserResponse;
import com.nitroxen.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with phone number and password")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }

    @GetMapping("/me")
    @Operation(summary = "Get logged-in user details", description = "Fetch details of the currently logged-in user from JWT token")
    public ResponseEntity<UserResponse> getLoggedInUserDetails(Authentication authentication) {
        UserResponse userResponse = authService.getLoggedInUserDetails(authentication);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/request-otp")
    @Operation(summary = "Request forgot password OTP",
               description = "Send OTP via WhatsApp for password reset using Twilio Verify API")
    public ResponseEntity<OtpResponse> requestForgotPasswordOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        OtpResponse response = authService.requestForgotPasswordOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP and reset password",
               description = "Verify WhatsApp OTP and reset user password")
    public ResponseEntity<OtpResponse> verifyOtpAndResetPassword(@Valid @RequestBody VerifyOtpRequest request) {
        OtpResponse response = authService.verifyOtpAndResetPassword(request);
        return ResponseEntity.ok(response);
    }
}
