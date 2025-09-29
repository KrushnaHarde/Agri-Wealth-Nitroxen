package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.request.PolyhouseRequest;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.Polyhouse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.entity.Zone;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.FarmRepository;
import com.nitroxen.demo.repository.PolyhouseRepository;
import com.nitroxen.demo.repository.ZoneRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PolyhouseServiceImplTest {

    @Mock
    private PolyhouseRepository polyhouseRepository;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @InjectMocks
    private PolyhouseServiceImpl polyhouseService;

    private User owner;
    private Farm farm;
    private Polyhouse polyhouse;
    private PolyhouseRequest polyhouseRequest;
    private Zone zone;

    private final Long ownerId = 1L;
    private final Long farmId = 1L;
    private final Long polyhouseId = 1L;
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
                .polyhouses(new ArrayList<>())
                .build();

        // Setup polyhouse
        polyhouse = Polyhouse.builder()
                .id(polyhouseId)
                .name("Test Polyhouse")
                .area(1000.0)
                .type("Gothic")
                .specifications("Test Specifications")
                .equipment("Test Equipment")
                .growingType("Hydroponic")
                .farm(farm)
                .zones(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup polyhouse request
        polyhouseRequest = new PolyhouseRequest();
        polyhouseRequest.setName("Test Polyhouse");
        polyhouseRequest.setArea(1000.0);
        polyhouseRequest.setType("Gothic");
        polyhouseRequest.setSpecifications("Test Specifications");
        polyhouseRequest.setEquipment("Test Equipment");
        polyhouseRequest.setGrowingType("Hydroponic");

        // Setup zone
        zone = Zone.builder()
                .id(zoneId)
                .name("Test Zone")
                .systemType("NFT")
                .cropType("Lettuce")
                .polyhouse(polyhouse)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Add zone to polyhouse
        List<Zone> zones = new ArrayList<>();
        zones.add(zone);
        polyhouse.setZones(zones);
    }

    @Test
    void createPolyhouse_Success() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(polyhouseRepository.existsByNameAndFarmId("Test Polyhouse", farmId)).thenReturn(false);
        when(polyhouseRepository.save(any(Polyhouse.class))).thenReturn(polyhouse);

        // Act
        PolyhouseResponse response = polyhouseService.createPolyhouse(farmId, polyhouseRequest, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(polyhouseId);
        assertThat(response.getName()).isEqualTo("Test Polyhouse");
        assertThat(response.getArea()).isEqualTo(1000.0);
        assertThat(response.getFarmId()).isEqualTo(farmId);
        verify(polyhouseRepository, times(1)).save(any(Polyhouse.class));
    }

    @Test
    void createPolyhouse_FarmNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> polyhouseService.createPolyhouse(farmId, polyhouseRequest, ownerId));
        verify(polyhouseRepository, never()).save(any(Polyhouse.class));
    }

    @Test
    void createPolyhouse_NotEnoughArea_ThrowsValidationException() {
        // Arrange
        Farm smallFarm = Farm.builder()
                .id(farmId)
                .name("Small Farm")
                .totalArea(500.0)  // Smaller than requested polyhouse area
                .owner(owner)
                .polyhouses(new ArrayList<>())
                .build();

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(smallFarm));

        // Act & Assert
        assertThrows(ValidationException.class, () -> polyhouseService.createPolyhouse(farmId, polyhouseRequest, ownerId));
        verify(polyhouseRepository, never()).save(any(Polyhouse.class));
    }

    @Test
    void createPolyhouse_DuplicateName_ThrowsValidationException() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(polyhouseRepository.existsByNameAndFarmId("Test Polyhouse", farmId)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> polyhouseService.createPolyhouse(farmId, polyhouseRequest, ownerId));
        verify(polyhouseRepository, never()).save(any(Polyhouse.class));
    }

    @Test
    void getPolyhousesByFarm_ReturnsListOfPolyhouses() {
        // Arrange
        List<Polyhouse> polyhouses = List.of(polyhouse);
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(polyhouseRepository.findByFarmId(farmId)).thenReturn(polyhouses);

        // Act
        List<PolyhouseResponse> responses = polyhouseService.getPolyhousesByFarm(farmId, ownerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(polyhouseId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Polyhouse");
        verify(polyhouseRepository, times(1)).findByFarmId(farmId);
    }

    @Test
    void getPolyhouseById_Success() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));

        // Act
        PolyhouseResponse response = polyhouseService.getPolyhouseById(polyhouseId, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(polyhouseId);
        assertThat(response.getName()).isEqualTo("Test Polyhouse");
        verify(polyhouseRepository, times(1)).findById(polyhouseId);
    }

    @Test
    void getPolyhouseById_NotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> polyhouseService.getPolyhouseById(polyhouseId, ownerId));
    }

    @Test
    void updatePolyhouse_Success() {
        // Arrange
        PolyhouseRequest updateRequest = new PolyhouseRequest();
        updateRequest.setName("Updated Polyhouse");
        updateRequest.setArea(1200.0);
        updateRequest.setType("Updated Type");
        updateRequest.setSpecifications("Updated Specifications");
        updateRequest.setEquipment("Updated Equipment");
        updateRequest.setGrowingType("Updated Growing Type");

        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(polyhouseRepository.existsByNameAndFarmId("Updated Polyhouse", farmId)).thenReturn(false);

        Polyhouse updatedPolyhouse = Polyhouse.builder()
                .id(polyhouseId)
                .name("Updated Polyhouse")
                .area(1200.0)
                .type("Updated Type")
                .specifications("Updated Specifications")
                .equipment("Updated Equipment")
                .growingType("Updated Growing Type")
                .farm(farm)
                .zones(polyhouse.getZones())
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(polyhouseRepository.save(any(Polyhouse.class))).thenReturn(updatedPolyhouse);

        // Act
        PolyhouseResponse response = polyhouseService.updatePolyhouse(polyhouseId, updateRequest, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Polyhouse");
        assertThat(response.getArea()).isEqualTo(1200.0);
        verify(polyhouseRepository, times(1)).save(any(Polyhouse.class));
    }

    @Test
    void deletePolyhouse_Success() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        doNothing().when(polyhouseRepository).delete(polyhouse);

        // Act
        polyhouseService.deletePolyhouse(polyhouseId, ownerId);

        // Assert
        verify(polyhouseRepository, times(1)).delete(polyhouse);
    }

    @Test
    void getPolyhouseWithZones_Success() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));

        // Act
        PolyhouseResponse response = polyhouseService.getPolyhouseWithZones(polyhouseId, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(polyhouseId);
        assertThat(response.getName()).isEqualTo("Test Polyhouse");
        assertThat(response.getZoneCount()).isEqualTo(1);
    }

    @Test
    void getZonesByPolyhouse_Success() {
        // Arrange
        List<Zone> zones = List.of(zone);
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(zoneRepository.findByPolyhouseId(polyhouseId)).thenReturn(zones);

        // Act
        List<ZoneResponse> responses = polyhouseService.getZonesByPolyhouse(polyhouseId, ownerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(zoneId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Zone");
        verify(zoneRepository, times(1)).findByPolyhouseId(polyhouseId);
    }
}
