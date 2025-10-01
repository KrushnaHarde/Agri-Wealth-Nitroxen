package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.*;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerServiceImplTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private PolyhouseRepository polyhouseRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private ReservoirRepository reservoirRepository;

    @Mock
    private FarmAssignmentRepository farmAssignmentRepository;

    @InjectMocks
    private ManagerServiceImpl managerService;

    private User owner;
    private User manager;
    private Farm farm;
    private Polyhouse polyhouse;
    private Zone zone;
    private Reservoir reservoir;
    private final Long ownerId = 1L;
    private final Long managerId = 2L;
    private final Long farmId = 1L;
    private final Long polyhouseId = 1L;
    private final Long zoneId = 1L;
    private final Long reservoirId = 1L;
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
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup manager
        manager = User.builder()
                .id(managerId)
                .name("Test Manager")
                .email("manager@test.com")
                .phoneNumber("+1234567891")
                .role(Role.MANAGER)
                .enabled(true)
                .createdBy(ownerId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup farm
        farm = Farm.builder()
                .id(farmId)
                .name("Test Farm")
                .location("Test Location")
                .totalArea(10000.0)
                .description("Test Description")
                .owner(owner)
                .polyhouses(new ArrayList<>())
                .reservoirs(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup polyhouse
        polyhouse = Polyhouse.builder()
                .id(polyhouseId)
                .name("Test Polyhouse")
                .area(1000.0)
                .type("Greenhouse")
                .farm(farm)
                .zones(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup zone
        zone = Zone.builder()
                .id(zoneId)
                .name("Test Zone")
                .systemType("Hydroponic")
                .cropType("Tomato")
                .polyhouse(polyhouse)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup reservoir
        reservoir = Reservoir.builder()
                .id(reservoirId)
                .name("Test Reservoir")
                .capacity(5000.0)
                .waterSource("Borewell")
                .farm(farm)
                .servingZones(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void getAssignedFarms_Success() {
        // Arrange
        List<Long> assignedFarmIds = List.of(farmId);
        when(farmAssignmentRepository.findAssignedFarmIdsByManagerId(managerId)).thenReturn(assignedFarmIds);
        when(farmRepository.findAllById(assignedFarmIds)).thenReturn(List.of(farm));

        // Act
        List<FarmResponse> responses = managerService.getAssignedFarms(managerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(farmId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Farm");
        verify(farmAssignmentRepository, times(1)).findAssignedFarmIdsByManagerId(managerId);
        verify(farmRepository, times(1)).findAllById(assignedFarmIds);
    }

    @Test
    void getAssignedFarmById_Success() {
        // Arrange
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(true);
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));

        // Act
        FarmResponse response = managerService.getAssignedFarmById(farmId, managerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(farmId);
        assertThat(response.getName()).isEqualTo("Test Farm");
        verify(farmAssignmentRepository, times(1)).existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId);
    }

    @Test
    void getAssignedFarmById_NotAssigned_ThrowsValidationException() {
        // Arrange
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> managerService.getAssignedFarmById(farmId, managerId));
        verify(farmRepository, never()).findById(anyLong());
    }

    @Test
    void getAssignedFarmById_FarmNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(true);
        when(farmRepository.findById(farmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> managerService.getAssignedFarmById(farmId, managerId));
    }

    @Test
    void getPolyhousesByAssignedFarm_Success() {
        // Arrange
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(true);
        when(polyhouseRepository.findByFarmId(farmId)).thenReturn(List.of(polyhouse));

        // Act
        List<PolyhouseResponse> responses = managerService.getPolyhousesByAssignedFarm(farmId, managerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(polyhouseId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Polyhouse");
        verify(polyhouseRepository, times(1)).findByFarmId(farmId);
    }

    @Test
    void getPolyhousesByAssignedFarm_NotAssigned_ThrowsValidationException() {
        // Arrange
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> managerService.getPolyhousesByAssignedFarm(farmId, managerId));
        verify(polyhouseRepository, never()).findByFarmId(anyLong());
    }

    @Test
    void getPolyhouseById_Success() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(true);

        // Act
        PolyhouseResponse response = managerService.getPolyhouseById(polyhouseId, managerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(polyhouseId);
        assertThat(response.getName()).isEqualTo("Test Polyhouse");
    }

    @Test
    void getPolyhouseById_NotInAssignedFarm_ThrowsValidationException() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(false);

        // Act & Assert
        assertThrows(ValidationException.class, () -> managerService.getPolyhouseById(polyhouseId, managerId));
    }

    @Test
    void getZonesByPolyhouse_Success() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(true);
        when(zoneRepository.findByPolyhouseId(polyhouseId)).thenReturn(List.of(zone));

        // Act
        List<ZoneResponse> responses = managerService.getZonesByPolyhouse(polyhouseId, managerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(zoneId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Zone");
    }

    @Test
    void getZoneById_Success() {
        // Arrange
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(true);

        // Act
        ZoneResponse response = managerService.getZoneById(zoneId, managerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(zoneId);
        assertThat(response.getName()).isEqualTo("Test Zone");
    }

    @Test
    void getReservoirsByFarm_Success() {
        // Arrange
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(true);
        when(reservoirRepository.findByFarmId(farmId)).thenReturn(List.of(reservoir));

        // Act
        List<ReservoirResponse> responses = managerService.getReservoirsByFarm(farmId, managerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(reservoirId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Reservoir");
    }

    @Test
    void isFarmAssignedToManager_ReturnsTrue() {
        // Arrange
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(true);

        // Act
        boolean result = managerService.isFarmAssignedToManager(farmId, managerId);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void isFarmAssignedToManager_ReturnsFalse() {
        // Arrange
        when(farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId)).thenReturn(false);

        // Act
        boolean result = managerService.isFarmAssignedToManager(farmId, managerId);

        // Assert
        assertThat(result).isFalse();
    }
}
