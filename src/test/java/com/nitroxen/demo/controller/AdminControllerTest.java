package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.CreateUserRequest;
import com.nitroxen.demo.dto.response.UserResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdminController adminController;

    private User admin;
    private CreateUserRequest createUserRequest;
    private UserResponse userResponse;

    private final Long adminId = 1L;
    private final Long ownerId = 2L;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup admin
        admin = User.builder()
                .id(adminId)
                .name("Test Admin")
                .email("admin@test.com")
                .phoneNumber("+1234567890")
                .role(Role.ADMIN)
                .build();

        // Setup create user request
        createUserRequest = new CreateUserRequest();
        createUserRequest.setName("Test Owner");
        createUserRequest.setEmail("owner@test.com");
        createUserRequest.setPhoneNumber("+0987654321");
        createUserRequest.setPassword("password");
        createUserRequest.setRole(Role.OWNER);

        // Setup user response
        userResponse = UserResponse.builder()
                .id(ownerId)
                .name("Test Owner")
                .email("owner@test.com")
                .phoneNumber("+0987654321")
                .role(Role.OWNER)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(adminId)
                .build();

        // Mock authentication to return the admin
        when(authentication.getPrincipal()).thenReturn(admin);
    }

    @Test
    void createOwner_Success() {
        // Arrange
        when(userService.createUser(any(CreateUserRequest.class), eq(adminId))).thenReturn(userResponse);

        // Act
        ResponseEntity<UserResponse> response = adminController.createOwner(createUserRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(userResponse);
        assertThat(response.getBody().getRole()).isEqualTo(Role.OWNER);
        assertThat(response.getBody().getCreatedBy()).isEqualTo(adminId);
    }

    @Test
    void getAllOwners_Success() {
        // Arrange
        List<UserResponse> ownerResponses = List.of(userResponse);
        when(userService.getUsersByRole(Role.OWNER)).thenReturn(ownerResponses);

        // Act
        ResponseEntity<List<UserResponse>> response = adminController.getAllOwners();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(userResponse);
    }

    // Note: Not testing getOwnerById and getRevenueTracking as they are placeholders
    // and don't contain actual implementation yet
}
