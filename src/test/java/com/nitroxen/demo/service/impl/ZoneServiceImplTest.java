package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.request.ZoneRequest;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.Polyhouse;
import com.nitroxen.demo.entity.Reservoir;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.entity.Zone;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.PolyhouseRepository;
import com.nitroxen.demo.repository.ReservoirRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ZoneServiceImplTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private PolyhouseRepository polyhouseRepository;

    @Mock
    private ReservoirRepository reservoirRepository;

    @InjectMocks
    private ZoneServiceImpl zoneService;

    private User owner;
    private Farm farm;
    private Polyhouse polyhouse;
    private Reservoir reservoir;
    private Zone zone;
    private ZoneRequest zoneRequest;

    private final Long ownerId = 1L;
    private final Long farmId = 1L;
    private final Long polyhouseId = 1L;
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
                .polyhouses(new ArrayList<>())
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

        // Setup zone
        zone = Zone.builder()
                .id(zoneId)
                .name("Test Zone")
                .systemType("NFT")
                .cropType("Lettuce")
                .cropVariety("Butterhead")
                .plantingConfiguration("15cm spacing")
                .irrigationSetup("Drip irrigation")
                .dosingSystem("Automated EC/pH control")
                .polyhouse(polyhouse)
                .waterSource(reservoir)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Setup zone request
        zoneRequest = new ZoneRequest();
        zoneRequest.setName("Test Zone");
        zoneRequest.setSystemType("NFT");
        zoneRequest.setCropType("Lettuce");
        zoneRequest.setCropVariety("Butterhead");
        zoneRequest.setPlantingConfiguration("15cm spacing");
        zoneRequest.setIrrigationSetup("Drip irrigation");
        zoneRequest.setDosingSystem("Automated EC/pH control");
        zoneRequest.setReservoirId(reservoirId);
    }

    @Test
    void createZone_Success() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(zoneRepository.existsByNameAndPolyhouseId("Test Zone", polyhouseId)).thenReturn(false);
        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.of(reservoir));
        when(zoneRepository.save(any(Zone.class))).thenReturn(zone);

        // Act
        ZoneResponse response = zoneService.createZone(polyhouseId, zoneRequest, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(zoneId);
        assertThat(response.getName()).isEqualTo("Test Zone");
        assertThat(response.getSystemType()).isEqualTo("NFT");
        assertThat(response.getPolyhouseId()).isEqualTo(polyhouseId);
        assertThat(response.getWaterSourceId()).isEqualTo(reservoirId);
        verify(zoneRepository, times(1)).save(any(Zone.class));
    }

    @Test
    void createZone_PolyhouseNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> zoneService.createZone(polyhouseId, zoneRequest, ownerId));
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void createZone_MaxZonesReached_ThrowsValidationException() {
        // Arrange
        // Create a polyhouse with 4 zones (max limit)
        Polyhouse fullPolyhouse = polyhouse;
        List<Zone> zones = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            zones.add(Zone.builder().id((long) i + 1).name("Zone " + (i + 1)).polyhouse(fullPolyhouse).build());
        }
        fullPolyhouse.setZones(zones);

        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(fullPolyhouse));

        // Act & Assert
        assertThrows(ValidationException.class, () -> zoneService.createZone(polyhouseId, zoneRequest, ownerId));
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void createZone_DuplicateName_ThrowsValidationException() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(zoneRepository.existsByNameAndPolyhouseId("Test Zone", polyhouseId)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> zoneService.createZone(polyhouseId, zoneRequest, ownerId));
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void createZone_ReservoirNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(zoneRepository.existsByNameAndPolyhouseId("Test Zone", polyhouseId)).thenReturn(false);
        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> zoneService.createZone(polyhouseId, zoneRequest, ownerId));
        verify(zoneRepository, never()).save(any(Zone.class));
    }

    @Test
    void getZoneById_Success() {
        // Arrange
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));

        // Act
        ZoneResponse response = zoneService.getZoneById(zoneId, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(zoneId);
        assertThat(response.getName()).isEqualTo("Test Zone");
        assertThat(response.getSystemType()).isEqualTo("NFT");
    }

    @Test
    void getZoneById_NotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> zoneService.getZoneById(zoneId, ownerId));
    }

    @Test
    void getZonesByPolyhouse_Success() {
        // Arrange
        List<Zone> zones = List.of(zone);
        when(polyhouseRepository.findById(polyhouseId)).thenReturn(Optional.of(polyhouse));
        when(zoneRepository.findByPolyhouseId(polyhouseId)).thenReturn(zones);

        // Act
        List<ZoneResponse> responses = zoneService.getZonesByPolyhouse(polyhouseId, ownerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(zoneId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Zone");
        verify(zoneRepository, times(1)).findByPolyhouseId(polyhouseId);
    }

    @Test
    void updateZone_Success() {
        // Arrange
        ZoneRequest updateRequest = new ZoneRequest();
        updateRequest.setName("Updated Zone");
        updateRequest.setSystemType("DWC");
        updateRequest.setCropType("Tomato");
        updateRequest.setCropVariety("Cherry");
        updateRequest.setPlantingConfiguration("30cm spacing");
        updateRequest.setIrrigationSetup("Updated irrigation");
        updateRequest.setDosingSystem("Updated dosing");
        updateRequest.setReservoirId(reservoirId);

        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        when(zoneRepository.existsByNameAndPolyhouseId("Updated Zone", polyhouseId)).thenReturn(false);
        when(reservoirRepository.findById(reservoirId)).thenReturn(Optional.of(reservoir));

        Zone updatedZone = Zone.builder()
                .id(zoneId)
                .name("Updated Zone")
                .systemType("DWC")
                .cropType("Tomato")
                .cropVariety("Cherry")
                .plantingConfiguration("30cm spacing")
                .irrigationSetup("Updated irrigation")
                .dosingSystem("Updated dosing")
                .polyhouse(polyhouse)
                .waterSource(reservoir)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(zoneRepository.save(any(Zone.class))).thenReturn(updatedZone);

        // Act
        ZoneResponse response = zoneService.updateZone(zoneId, updateRequest, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Zone");
        assertThat(response.getSystemType()).isEqualTo("DWC");
        assertThat(response.getCropType()).isEqualTo("Tomato");
        verify(zoneRepository, times(1)).save(any(Zone.class));
    }

    @Test
    void deleteZone_Success() {
        // Arrange
        when(zoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
        doNothing().when(zoneRepository).delete(zone);

        // Act
        zoneService.deleteZone(zoneId, ownerId);

        // Assert
        verify(zoneRepository, times(1)).delete(zone);
    }
}
