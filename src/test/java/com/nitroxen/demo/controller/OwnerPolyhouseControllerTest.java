package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.PolyhouseRequest;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.service.PolyhouseService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerPolyhouseControllerTest {

    @Mock
    private PolyhouseService polyhouseService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OwnerPolyhouseController ownerPolyhouseController;

    private User owner;
    private PolyhouseRequest polyhouseRequest;
    private PolyhouseResponse polyhouseResponse;
    private ZoneResponse zoneResponse;

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
                .build();

        // Setup polyhouse request
        polyhouseRequest = new PolyhouseRequest();
        polyhouseRequest.setName("Test Polyhouse");
        polyhouseRequest.setArea(1000.0);
        polyhouseRequest.setType("Gothic");
        polyhouseRequest.setSpecifications("Test Specifications");
        polyhouseRequest.setEquipment("Test Equipment");
        polyhouseRequest.setGrowingType("Hydroponic");

        // Setup polyhouse response
        polyhouseResponse = PolyhouseResponse.builder()
                .id(polyhouseId)
                .name("Test Polyhouse")
                .area(1000.0)
                .type("Gothic")
                .specifications("Test Specifications")
                .equipment("Test Equipment")
                .growingType("Hydroponic")
                .farmId(farmId)
                .farmName("Test Farm")
                .zoneCount(0)
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

        // Mock authentication to return the owner
        when(authentication.getPrincipal()).thenReturn(owner);
    }

    @Test
    void createPolyhouse_Success() {
        // Arrange
        when(polyhouseService.createPolyhouse(eq(farmId), any(PolyhouseRequest.class), eq(ownerId))).thenReturn(polyhouseResponse);

        // Act
        ResponseEntity<PolyhouseResponse> response = ownerPolyhouseController.createPolyhouse(farmId, polyhouseRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(polyhouseResponse);
        verify(polyhouseService, times(1)).createPolyhouse(eq(farmId), any(PolyhouseRequest.class), eq(ownerId));
    }

    @Test
    void getPolyhousesByFarm_Success() {
        // Arrange
        List<PolyhouseResponse> polyhouseResponses = List.of(polyhouseResponse);
        when(polyhouseService.getPolyhousesByFarm(farmId, ownerId)).thenReturn(polyhouseResponses);

        // Act
        ResponseEntity<List<PolyhouseResponse>> response = ownerPolyhouseController.getPolyhousesByFarm(farmId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(polyhouseResponse);
        verify(polyhouseService, times(1)).getPolyhousesByFarm(farmId, ownerId);
    }

    @Test
    void getPolyhouseById_Success() {
        // Arrange
        when(polyhouseService.getPolyhouseById(polyhouseId, ownerId)).thenReturn(polyhouseResponse);

        // Act
        ResponseEntity<PolyhouseResponse> response = ownerPolyhouseController.getPolyhouseById(polyhouseId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(polyhouseResponse);
        verify(polyhouseService, times(1)).getPolyhouseById(polyhouseId, ownerId);
    }

    @Test
    void updatePolyhouse_Success() {
        // Arrange
        PolyhouseResponse updatedResponse = PolyhouseResponse.builder()
                .id(polyhouseId)
                .name("Updated Polyhouse")
                .area(1500.0)
                .type("Updated Type")
                .specifications("Updated Specifications")
                .equipment("Updated Equipment")
                .growingType("Updated Growing Type")
                .farmId(farmId)
                .farmName("Test Farm")
                .zoneCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(polyhouseService.updatePolyhouse(eq(polyhouseId), any(PolyhouseRequest.class), eq(ownerId))).thenReturn(updatedResponse);

        // Act
        ResponseEntity<PolyhouseResponse> response = ownerPolyhouseController.updatePolyhouse(polyhouseId, polyhouseRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedResponse);
        verify(polyhouseService, times(1)).updatePolyhouse(eq(polyhouseId), any(PolyhouseRequest.class), eq(ownerId));
    }

    @Test
    void deletePolyhouse_Success() {
        // Arrange
        doNothing().when(polyhouseService).deletePolyhouse(polyhouseId, ownerId);

        // Act
        ResponseEntity<Void> response = ownerPolyhouseController.deletePolyhouse(polyhouseId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(polyhouseService, times(1)).deletePolyhouse(polyhouseId, ownerId);
    }

    @Test
    void getPolyhouseWithZones_Success() {
        // Arrange
        PolyhouseResponse polyhouseWithZones = PolyhouseResponse.builder()
                .id(polyhouseId)
                .name("Test Polyhouse")
                .area(1000.0)
                .type("Gothic")
                .specifications("Test Specifications")
                .equipment("Test Equipment")
                .growingType("Hydroponic")
                .farmId(farmId)
                .farmName("Test Farm")
                .zoneCount(1)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(polyhouseService.getPolyhouseWithZones(polyhouseId, ownerId)).thenReturn(polyhouseWithZones);

        // Act
        ResponseEntity<PolyhouseResponse> response = ownerPolyhouseController.getPolyhouseWithZones(polyhouseId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(polyhouseWithZones);
        assertThat(response.getBody().getZoneCount()).isEqualTo(1);
        verify(polyhouseService, times(1)).getPolyhouseWithZones(polyhouseId, ownerId);
    }

    @Test
    void getZonesByPolyhouse_Success() {
        // Arrange
        List<ZoneResponse> zoneResponses = List.of(zoneResponse);
        when(polyhouseService.getZonesByPolyhouse(polyhouseId, ownerId)).thenReturn(zoneResponses);

        // Act
        ResponseEntity<List<ZoneResponse>> response = ownerPolyhouseController.getZonesByPolyhouse(polyhouseId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(zoneResponse);
        verify(polyhouseService, times(1)).getZonesByPolyhouse(polyhouseId, ownerId);
    }
}
