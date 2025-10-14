package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.request.ReservoirRequest;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.Reservoir;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.entity.Zone;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.FarmRepository;
import com.nitroxen.demo.repository.ReservoirRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservoirServiceImplTest {

    @Mock
    private ReservoirRepository reservoirRepository;

    @Mock
    private FarmRepository farmRepository;

    @InjectMocks
    private ReservoirServiceImpl reservoirService;

    private User owner;
    private Farm farm;
    private Reservoir reservoir;
    private ReservoirRequest reservoirRequest;
    private Zone zone;

    private final Long ownerId = 1L;
    private final Long farmId = 1L;
    private final Long reservoirId = 1L;
    private final Long zoneId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup owner
        owner = User.builder()
                .id(ownerId)
                .name("Test Owner")
                .email("owner@test.com")
                .phoneNumber("+1234567890")
                .password("password")
                .role(Role.OWNER)
                .enabled(true)
                .build();

        // Setup farm
        farm = Farm.builder()
                .id(farmId)
                .name("Test Farm")
                .location("Test Location")
                .totalArea(10000.0)
                .description("Test Description")
                .owner(owner)
                .reservoirs(new ArrayList<>())
                .build();

        // Setup reservoir
        reservoir = Reservoir.builder()
                .id(reservoirId)
                .name("Test Reservoir")
                .capacity(5000.0)
                .waterSource("Municipal")
                .waterTreatment("Filtration")
                .farm(farm)
                .servingZones(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup reservoir request
        reservoirRequest = new ReservoirRequest();
        reservoirRequest.setName("Test Reservoir");
        reservoirRequest.setCapacity(5000.0);
        reservoirRequest.setWaterSource("Municipal");
        reservoirRequest.setWaterTreatment("Filtration");

        // Setup zone that uses the reservoir
        zone = Zone.builder()
                .id(zoneId)
                .name("Test Zone")
                .waterSource(reservoir)
                .build();
    }

    @Test
    void createReservoir_Success() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(reservoirRepository.existsByNameAndFarmId("Test Reservoir", farmId)).thenReturn(false);
        when(reservoirRepository.save(any(Reservoir.class))).thenReturn(reservoir);

        // Act
        ReservoirResponse response = reservoirService.createReservoir(farmId, reservoirRequest, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(reservoirId);
        assertThat(response.getName()).isEqualTo("Test Reservoir");
        assertThat(response.getCapacity()).isEqualTo(5000.0);
        assertThat(response.getFarmId()).isEqualTo(farmId);
        verify(reservoirRepository, times(1)).save(any(Reservoir.class));
    }

    @Test
    void createReservoir_FarmNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reservoirService.createReservoir(farmId, reservoirRequest, ownerId));
        verify(reservoirRepository, never()).save(any(Reservoir.class));
    }

    @Test
    void createReservoir_DuplicateName_ThrowsValidationException() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(reservoirRepository.existsByNameAndFarmId("Test Reservoir", farmId)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> reservoirService.createReservoir(farmId, reservoirRequest, ownerId));
        verify(reservoirRepository, never()).save(any(Reservoir.class));
    }

    @Test
    void getReservoirsByFarm_ReturnsListOfReservoirs() {
        // Arrange
        List<Reservoir> reservoirs = List.of(reservoir);
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(reservoirRepository.findByFarmId(farmId)).thenReturn(reservoirs);

        // Act
        List<ReservoirResponse> responses = reservoirService.getReservoirsByFarm(farmId, ownerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(reservoirId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Reservoir");
        verify(reservoirRepository, times(1)).findByFarmId(farmId);
    }

    @Test
    void getReservoirById_Success() {
        // Arrange
        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.of(reservoir));

        // Act
        ReservoirResponse response = reservoirService.getReservoirById(reservoirId, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(reservoirId);
        assertThat(response.getName()).isEqualTo("Test Reservoir");
        verify(reservoirRepository, times(1)).findById(reservoirId);
    }

    @Test
    void getReservoirById_NotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> reservoirService.getReservoirById(reservoirId, ownerId));
    }

    @Test
    void updateReservoir_Success() {
        // Arrange
        ReservoirRequest updateRequest = new ReservoirRequest();
        updateRequest.setName("Updated Reservoir");
        updateRequest.setCapacity(7000.0);
        updateRequest.setWaterSource("Well Water");
        updateRequest.setWaterTreatment("RO System");

        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.of(reservoir));
        when(reservoirRepository.existsByNameAndFarmId("Updated Reservoir", farmId)).thenReturn(false);

        Reservoir updatedReservoir = Reservoir.builder()
                .id(reservoirId)
                .name("Updated Reservoir")
                .capacity(7000.0)
                .waterSource("Well Water")
                .waterTreatment("RO System")
                .farm(farm)
                .servingZones(reservoir.getServingZones())
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(reservoirRepository.save(any(Reservoir.class))).thenReturn(updatedReservoir);

        // Act
        ReservoirResponse response = reservoirService.updateReservoir(reservoirId, updateRequest, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Reservoir");
        assertThat(response.getCapacity()).isEqualTo(7000.0);
        assertThat(response.getWaterSource()).isEqualTo("Well Water");
        verify(reservoirRepository, times(1)).save(any(Reservoir.class));
    }

    @Test
    void updateReservoir_DuplicateName_ThrowsValidationException() {
        // Arrange
        ReservoirRequest updateRequest = new ReservoirRequest();
        updateRequest.setName("Other Reservoir");
        updateRequest.setCapacity(5000.0);
        updateRequest.setWaterSource("Municipal");
        updateRequest.setWaterTreatment("Filtration");

        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.of(reservoir));
        when(reservoirRepository.existsByNameAndFarmId("Other Reservoir", farmId)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> reservoirService.updateReservoir(reservoirId, updateRequest, ownerId));
        verify(reservoirRepository, never()).save(any(Reservoir.class));
    }

    @Test
    void deleteReservoir_Success() {
        // Arrange
        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.of(reservoir));
        doNothing().when(reservoirRepository).delete(reservoir);

        // Act
        reservoirService.deleteReservoir(reservoirId, ownerId);

        // Assert
        verify(reservoirRepository, times(1)).delete(reservoir);
    }

    @Test
    void deleteReservoir_InUseByZones_ThrowsValidationException() {
        // Arrange
        Reservoir reservoirInUse = reservoir;
        List<Zone> servingZones = new ArrayList<>();
        servingZones.add(zone);
        reservoirInUse.setServingZones(servingZones);

        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.of(reservoirInUse));

        // Act & Assert
        assertThrows(ValidationException.class, () -> reservoirService.deleteReservoir(reservoirId, ownerId));
        verify(reservoirRepository, never()).delete(any(Reservoir.class));
    }
}
