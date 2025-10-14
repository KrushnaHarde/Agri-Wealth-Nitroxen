package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.ChangePasswordRequest;
import com.nitroxen.demo.dto.request.LoginRequest;
import com.nitroxen.demo.dto.response.AuthResponse;
import com.nitroxen.demo.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private ChangePasswordRequest passwordRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setPhoneNumber("+1234567890");
        loginRequest.setPassword("password");

        // Setup change password request
        passwordRequest = new ChangePasswordRequest();
        passwordRequest.setPhoneNumber("+1234567890");
        passwordRequest.setCurrentPassword("currentPassword");
        passwordRequest.setNewPassword("newPassword");

        // Setup authentication response
        authResponse = new AuthResponse();
        authResponse.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        authResponse.setUserId(1L);
        authResponse.setName("Test User");
        authResponse.setRole("OWNER");
    }

    @Test
    void login_Success() {
        // Arrange
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(authResponse);
        assertThat(response.getBody().getToken()).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    void changePassword_Success() {
        // Arrange
        doNothing().when(authService).changePassword(any(ChangePasswordRequest.class));

        // Act
        ResponseEntity<String> response = authController.changePassword(passwordRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Password changed successfully");
        verify(authService, times(1)).changePassword(any(ChangePasswordRequest.class));
    }
}
