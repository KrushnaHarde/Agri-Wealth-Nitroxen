package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.CreateUserRequest;
import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.dto.response.UserResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.service.ManagerService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class ManagerControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ManagerService managerService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ManagerController managerController;

    private User manager;
    private UserResponse workerResponse;
    private FarmResponse farmResponse;
    private PolyhouseResponse polyhouseResponse;
    private ZoneResponse zoneResponse;
    private ReservoirResponse reservoirResponse;
    private CreateUserRequest createWorkerRequest;

    private final Long managerId = 1L;
    private final Long farmId = 1L;
    private final Long polyhouseId = 1L;
    private final Long zoneId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup manager
        manager = User.builder()
                .id(managerId)
                .name("Test Manager")
                .email("manager@test.com")
                .phoneNumber("+1234567890")
                .role(Role.MANAGER)
                .enabled(true)
                .build();

        // Setup worker response
        workerResponse = UserResponse.builder()
                .id(2L)
                .name("Test Worker")
                .email("worker@test.com")
                .phoneNumber("+0987654321")
                .role(Role.WORKER)
                .enabled(true)
                .createdBy(managerId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup farm response
        farmResponse = FarmResponse.builder()
                .id(farmId)
                .name("Test Farm")
                .location("Test Location")
                .totalArea(10000.0)
                .usedArea(1000.0)
                .remainingArea(9000.0)
                .ownerId(3L)
                .ownerName("Test Owner")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup polyhouse response
        polyhouseResponse = PolyhouseResponse.builder()
                .id(polyhouseId)
                .name("Test Polyhouse")
                .area(1000.0)
                .type("Gothic")
                .farmId(farmId)
                .farmName("Test Farm")
                .zoneCount(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup zone response
        zoneResponse = ZoneResponse.builder()
                .id(zoneId)
                .name("Test Zone")
                .systemType("NFT")
                .cropType("Lettuce")
                .polyhouseId(polyhouseId)
                .polyhouseName("Test Polyhouse")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup reservoir response
        reservoirResponse = ReservoirResponse.builder()
                .id(1L)
                .name("Test Reservoir")
                .capacity(5000.0)
                .waterSource("Municipal")
                .farmId(farmId)
                .farmName("Test Farm")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup create worker request
        createWorkerRequest = new CreateUserRequest();
        createWorkerRequest.setName("Test Worker");
        createWorkerRequest.setEmail("worker@test.com");
        createWorkerRequest.setPhoneNumber("+0987654321");
        createWorkerRequest.setPassword("password");

        // Setup security context
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(manager);
    }

    @Test
    void createWorker_Success() {
        // Arrange
        when(userService.createUser(any(CreateUserRequest.class), eq(managerId))).thenReturn(workerResponse);

        // Act
        ResponseEntity<UserResponse> response = managerController.createWorker(createWorkerRequest);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(workerResponse);
        assertThat(response.getBody().getRole()).isEqualTo(Role.WORKER);
        verify(userService, times(1)).createUser(any(CreateUserRequest.class), eq(managerId));
    }

    @Test
    void getAssignedWorkers_Success() {
        // Arrange
        List<UserResponse> workers = List.of(workerResponse);
        when(userService.getUsersCreatedBy(managerId, Role.WORKER)).thenReturn(workers);

        // Act
        ResponseEntity<List<UserResponse>> response = managerController.getAssignedWorkers();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(workerResponse);
        verify(userService, times(1)).getUsersCreatedBy(managerId, Role.WORKER);
    }

    @Test
    void getAssignedFarms_Success() {
        // Arrange
        List<FarmResponse> farms = List.of(farmResponse);
        when(managerService.getAssignedFarms(managerId)).thenReturn(farms);

        // Act
        ResponseEntity<List<FarmResponse>> response = managerController.getAssignedFarms();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(farmResponse);
        verify(managerService, times(1)).getAssignedFarms(managerId);
    }

    @Test
    void getAssignedFarmById_Success() {
        // Arrange
        when(managerService.getAssignedFarmById(farmId, managerId)).thenReturn(farmResponse);

        // Act
        ResponseEntity<FarmResponse> response = managerController.getAssignedFarmById(farmId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(farmResponse);
        verify(managerService, times(1)).getAssignedFarmById(farmId, managerId);
    }

    @Test
    void getPolyhousesByFarm_Success() {
        // Arrange
        List<PolyhouseResponse> polyhouses = List.of(polyhouseResponse);
        when(managerService.getPolyhousesByAssignedFarm(farmId, managerId)).thenReturn(polyhouses);

        // Act
        ResponseEntity<List<PolyhouseResponse>> response = managerController.getPolyhousesByFarm(farmId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(polyhouseResponse);
        verify(managerService, times(1)).getPolyhousesByAssignedFarm(farmId, managerId);
    }

    @Test
    void getPolyhouseById_Success() {
        // Arrange
        when(managerService.getPolyhouseById(polyhouseId, managerId)).thenReturn(polyhouseResponse);

        // Act
        ResponseEntity<PolyhouseResponse> response = managerController.getPolyhouseById(polyhouseId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(polyhouseResponse);
        verify(managerService, times(1)).getPolyhouseById(polyhouseId, managerId);
    }

    @Test
    void getZonesByPolyhouse_Success() {
        // Arrange
        List<ZoneResponse> zones = List.of(zoneResponse);
        when(managerService.getZonesByPolyhouse(polyhouseId, managerId)).thenReturn(zones);

        // Act
        ResponseEntity<List<ZoneResponse>> response = managerController.getZonesByPolyhouse(polyhouseId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(zoneResponse);
        verify(managerService, times(1)).getZonesByPolyhouse(polyhouseId, managerId);
    }

    @Test
    void getZoneById_Success() {
        // Arrange
        when(managerService.getZoneById(zoneId, managerId)).thenReturn(zoneResponse);

        // Act
        ResponseEntity<ZoneResponse> response = managerController.getZoneById(zoneId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(zoneResponse);
        verify(managerService, times(1)).getZoneById(zoneId, managerId);
    }

    @Test
    void getReservoirsByFarm_Success() {
        // Arrange
        List<ReservoirResponse> reservoirs = List.of(reservoirResponse);
        when(managerService.getReservoirsByFarm(farmId, managerId)).thenReturn(reservoirs);

        // Act
        ResponseEntity<List<ReservoirResponse>> response = managerController.getReservoirsByFarm(farmId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(reservoirResponse);
        verify(managerService, times(1)).getReservoirsByFarm(farmId, managerId);
    }
}
