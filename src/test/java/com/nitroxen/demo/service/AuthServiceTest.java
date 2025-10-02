package com.nitroxen.demo.service;

import com.nitroxen.demo.config.JwtService;
import com.nitroxen.demo.dto.request.ChangePasswordRequest;
import com.nitroxen.demo.dto.request.LoginRequest;
import com.nitroxen.demo.dto.response.AuthResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private ChangePasswordRequest passwordRequest;
    private User user;
    private final String jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";
    private final Long expirationTime = 3600000L; // 1 hour in milliseconds

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

        // Setup user
        user = User.builder()
                .id(1L)
                .name("Test User")
                .phoneNumber("+1234567890")
                .email("user@test.com")
                .password("encodedPassword")
                .role(Role.OWNER)
                .enabled(true)
                .build();
    }

    @Test
    void login_Success() {
        // Arrange
        when(userService.findByPhoneNumber(loginRequest.getPhoneNumber())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn(jwtToken);
        when(jwtService.getExpirationTime()).thenReturn(expirationTime);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(jwtToken);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getExpiresIn()).isEqualTo(expirationTime / 1000); // Convert to seconds

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).findByPhoneNumber(loginRequest.getPhoneNumber());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    void changePassword_Success() {
        // Arrange
        doNothing().when(userService).changePassword(
                eq(passwordRequest.getPhoneNumber()),
                eq(passwordRequest.getCurrentPassword()),
                eq(passwordRequest.getNewPassword())
        );

        // Act
        authService.changePassword(passwordRequest);

        // Assert
        verify(userService, times(1)).changePassword(
                eq(passwordRequest.getPhoneNumber()),
                eq(passwordRequest.getCurrentPassword()),
                eq(passwordRequest.getNewPassword())
        );
    }
}
