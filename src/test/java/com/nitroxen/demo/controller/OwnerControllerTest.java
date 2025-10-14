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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private OwnerController ownerController;

    private User owner;
    private CreateUserRequest createManagerRequest;
    private CreateUserRequest createWorkerRequest;
    private UserResponse managerResponse;
    private UserResponse workerResponse;
    private final Long ownerId = 1L;
    private final Long managerId = 2L;
    private final Long workerId = 3L;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup owner
        owner = User.builder()
                .id(ownerId)
                .name("Test Owner")
                .email("owner@test.com")
                .phoneNumber("+1234567890")
                .role(Role.OWNER)
                .enabled(true)
                .build();

        // Setup create manager request
        createManagerRequest = new CreateUserRequest();
        createManagerRequest.setName("Test Manager");
        createManagerRequest.setEmail("manager@test.com");
        createManagerRequest.setPhoneNumber("+1234567891");
        createManagerRequest.setPassword("password");
        createManagerRequest.setRole(Role.MANAGER);

        // Setup create worker request
        createWorkerRequest = new CreateUserRequest();
        createWorkerRequest.setName("Test Worker");
        createWorkerRequest.setEmail("worker@test.com");
        createWorkerRequest.setPhoneNumber("+1234567892");
        createWorkerRequest.setPassword("password");
        createWorkerRequest.setRole(Role.WORKER);

        // Setup manager response
        managerResponse = UserResponse.builder()
                .id(managerId)
                .name("Test Manager")
                .email("manager@test.com")
                .phoneNumber("+1234567891")
                .role(Role.MANAGER)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(ownerId)
                .build();

        // Setup worker response
        workerResponse = UserResponse.builder()
                .id(workerId)
                .name("Test Worker")
                .email("worker@test.com")
                .phoneNumber("+1234567892")
                .role(Role.WORKER)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(ownerId)
                .build();

        // Setup security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(owner);
    }

    @Test
    void createManager_Success() {
        // Arrange
        when(userService.createUser(any(CreateUserRequest.class), eq(ownerId))).thenReturn(managerResponse);

        // Act
        ResponseEntity<UserResponse> response = ownerController.createManager(createManagerRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRole()).isEqualTo(Role.MANAGER);
        assertThat(response.getBody().getCreatedBy()).isEqualTo(ownerId);
        verify(userService, times(1)).createUser(any(CreateUserRequest.class), eq(ownerId));
    }

    @Test
    void createWorker_Success() {
        // Arrange
        when(userService.createUser(any(CreateUserRequest.class), eq(ownerId))).thenReturn(workerResponse);

        // Act
        ResponseEntity<UserResponse> response = ownerController.createWorker(createWorkerRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getRole()).isEqualTo(Role.WORKER);
        assertThat(response.getBody().getCreatedBy()).isEqualTo(ownerId);
        verify(userService, times(1)).createUser(any(CreateUserRequest.class), eq(ownerId));
    }

    @Test
    void getAllManagers_Success() {
        // Arrange
        List<UserResponse> managerResponses = List.of(managerResponse);
        when(userService.getUsersCreatedBy(ownerId, Role.MANAGER)).thenReturn(managerResponses);

        // Act
        ResponseEntity<List<UserResponse>> response = ownerController.getAllManagers();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getRole()).isEqualTo(Role.MANAGER);
        verify(userService, times(1)).getUsersCreatedBy(ownerId, Role.MANAGER);
    }

    @Test
    void getAllWorkers_Success() {
        // Arrange
        List<UserResponse> workerResponses = List.of(workerResponse);
        when(userService.getUsersCreatedBy(ownerId, Role.WORKER)).thenReturn(workerResponses);

        // Act
        ResponseEntity<List<UserResponse>> response = ownerController.getAllWorkers();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getRole()).isEqualTo(Role.WORKER);
        verify(userService, times(1)).getUsersCreatedBy(ownerId, Role.WORKER);
    }

    @Test
    void createManager_EnsuresRoleIsManager() {
        // Arrange
        createManagerRequest.setRole(Role.WORKER); // Try to set wrong role
        when(userService.createUser(any(CreateUserRequest.class), eq(ownerId))).thenReturn(managerResponse);

        // Act
        ResponseEntity<UserResponse> response = ownerController.createManager(createManagerRequest);

        // Assert
        assertThat(createManagerRequest.getRole()).isEqualTo(Role.MANAGER); // Should be overridden
        assertThat(response.getBody().getRole()).isEqualTo(Role.MANAGER);
    }

    @Test
    void createWorker_EnsuresRoleIsWorker() {
        // Arrange
        createWorkerRequest.setRole(Role.MANAGER); // Try to set wrong role
        when(userService.createUser(any(CreateUserRequest.class), eq(ownerId))).thenReturn(workerResponse);

        // Act
        ResponseEntity<UserResponse> response = ownerController.createWorker(createWorkerRequest);

        // Assert
        assertThat(createWorkerRequest.getRole()).isEqualTo(Role.WORKER); // Should be overridden
        assertThat(response.getBody().getRole()).isEqualTo(Role.WORKER);
    }
}
