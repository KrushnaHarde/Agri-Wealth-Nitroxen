package com.nitroxen.demo.service;

import com.nitroxen.demo.config.JwtService;
import com.nitroxen.demo.dto.request.ChangePasswordRequest;
import com.nitroxen.demo.dto.request.ForgotPasswordRequest;
import com.nitroxen.demo.dto.request.LoginRequest;
import com.nitroxen.demo.dto.request.VerifyOtpRequest;
import com.nitroxen.demo.dto.response.AuthResponse;
import com.nitroxen.demo.dto.response.OtpResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.dto.response.UserResponse;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final TwilioService twilioService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getPhoneNumber(),
                        request.getPassword()
                )
        );

        User user = userService.findByPhoneNumber(request.getPhoneNumber());
        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime() / 1000) // Convert to seconds
                .build();
    }

    public void changePassword(ChangePasswordRequest request) {
        // Use the phone number from the request, not from the authentication context
        userService.changePassword(request.getPhoneNumber(), request.getCurrentPassword(), request.getNewPassword());
    }

    public UserResponse getLoggedInUserDetails(String username) {
        User user = userService.findByPhoneNumber(username);
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedDate())
                .updatedAt(user.getLastModifiedDate())
                .build();
    }

    /**
     * Request OTP for forgot password via WhatsApp (with SMS fallback)
     */
    public OtpResponse requestForgotPasswordOtp(ForgotPasswordRequest request) {
        try {
            // Verify that user exists with this phone number
            User user = userService.findByPhoneNumber(request.getPhoneNumber());

            // Send OTP via WhatsApp using Twilio Verify API (with SMS fallback)
            Verification verification = twilioService.sendWhatsAppOTP(request.getPhoneNumber());

            // Determine which channel was actually used based on the response
            String channel = verification.getChannel().toString().toLowerCase();
            String message = "whatsapp".equals(channel) ?
                "OTP sent successfully via WhatsApp" :
                "OTP sent successfully via SMS (WhatsApp unavailable)";

            return OtpResponse.builder()
                    .status(verification.getStatus())
                    .message(message)
                    .phoneNumber(maskPhoneNumber(request.getPhoneNumber()))
                    .success(true)
                    .build();

        } catch (ResourceNotFoundException e) {
            log.warn("Forgot password attempt for non-existent phone number: {}",
                    maskPhoneNumber(request.getPhoneNumber()));
            throw new ValidationException("No account found with this phone number");
        } catch (Exception e) {
            log.error("Failed to send forgot password OTP: {}", e.getMessage());
            throw new ValidationException("Failed to send OTP. Please check your phone number and try again.");
        }
    }

    /**
     * Verify OTP and reset password
     */
    public OtpResponse verifyOtpAndResetPassword(VerifyOtpRequest request) {
        try {
            // Verify that user exists
            User user = userService.findByPhoneNumber(request.getPhoneNumber());

            // Verify OTP using Twilio Verify API
            VerificationCheck verificationCheck = twilioService.verifyOTP(
                    request.getPhoneNumber(),
                    request.getOtpCode()
            );

            if (!verificationCheck.getValid() || !"approved".equals(verificationCheck.getStatus())) {
                throw new ValidationException("Invalid or expired OTP code");
            }

            // Reset password
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userService.saveUser(user);

            log.info("Password reset successful for user: {}", maskPhoneNumber(request.getPhoneNumber()));

            return OtpResponse.builder()
                    .status("approved")
                    .message("Password reset successfully")
                    .phoneNumber(maskPhoneNumber(request.getPhoneNumber()))
                    .success(true)
                    .build();

        } catch (ResourceNotFoundException e) {
            throw new ValidationException("No account found with this phone number");
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to verify OTP and reset password: {}", e.getMessage());
            throw new ValidationException("Failed to reset password. Please try again.");
        }
    }

    /**
     * Mask phone number for security logging
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "****";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
