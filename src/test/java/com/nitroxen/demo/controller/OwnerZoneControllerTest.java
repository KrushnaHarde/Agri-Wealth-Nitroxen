package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.ZoneRequest;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.service.ZoneService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OwnerZoneControllerTest {

    @Mock
    private ZoneService zoneService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OwnerZoneController ownerZoneController;

    private User owner;
    private ZoneRequest zoneRequest;
    private ZoneResponse zoneResponse;

    private final Long ownerId = 1L;
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

        // Setup zone response
        zoneResponse = ZoneResponse.builder()
                .id(zoneId)
                .name("Test Zone")
                .systemType("NFT")
                .cropType("Lettuce")
                .cropVariety("Butterhead")
                .plantingConfiguration("15cm spacing")
                .irrigationSetup("Drip irrigation")
                .dosingSystem("Automated EC/pH control")
                .polyhouseId(polyhouseId)
                .polyhouseName("Test Polyhouse")
                .waterSourceId(reservoirId)
                .waterSourceName("Test Reservoir")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Mock authentication to return the owner
        when(authentication.getPrincipal()).thenReturn(owner);
    }

    @Test
    void createZone_Success() {
        // Arrange
        when(zoneService.createZone(eq(polyhouseId), any(ZoneRequest.class), eq(ownerId))).thenReturn(zoneResponse);

        // Act
        ResponseEntity<ZoneResponse> response = ownerZoneController.createZone(polyhouseId, zoneRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(zoneResponse);
        verify(zoneService, times(1)).createZone(eq(polyhouseId), any(ZoneRequest.class), eq(ownerId));
    }

    @Test
    void getZonesByPolyhouse_Success() {
        // Arrange
        List<ZoneResponse> zoneResponses = List.of(zoneResponse);
        when(zoneService.getZonesByPolyhouse(polyhouseId, ownerId)).thenReturn(zoneResponses);

        // Act
        ResponseEntity<List<ZoneResponse>> response = ownerZoneController.getZonesByPolyhouse(polyhouseId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(zoneResponse);
        verify(zoneService, times(1)).getZonesByPolyhouse(polyhouseId, ownerId);
    }

    @Test
    void getZoneById_Success() {
        // Arrange
        when(zoneService.getZoneById(zoneId, ownerId)).thenReturn(zoneResponse);

        // Act
        ResponseEntity<ZoneResponse> response = ownerZoneController.getZoneById(polyhouseId, zoneId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(zoneResponse);
        verify(zoneService, times(1)).getZoneById(zoneId, ownerId);
    }

    @Test
    void updateZone_Success() {
        // Arrange
        ZoneResponse updatedResponse = ZoneResponse.builder()
                .id(zoneId)
                .name("Updated Zone")
                .systemType("DWC")
                .cropType("Tomato")
                .cropVariety("Cherry")
                .plantingConfiguration("30cm spacing")
                .irrigationSetup("Updated irrigation")
                .dosingSystem("Updated dosing")
                .polyhouseId(polyhouseId)
                .polyhouseName("Test Polyhouse")
                .waterSourceId(reservoirId)
                .waterSourceName("Test Reservoir")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(zoneService.updateZone(eq(zoneId), any(ZoneRequest.class), eq(ownerId))).thenReturn(updatedResponse);

        // Act
        ResponseEntity<ZoneResponse> response = ownerZoneController.updateZone(zoneId, zoneRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedResponse);
        verify(zoneService, times(1)).updateZone(eq(zoneId), any(ZoneRequest.class), eq(ownerId));
    }

    @Test
    void deleteZone_Success() {
        // Arrange
        doNothing().when(zoneService).deleteZone(zoneId, ownerId);

        // Act
        ResponseEntity<Void> response = ownerZoneController.deleteZone(zoneId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(zoneService, times(1)).deleteZone(zoneId, ownerId);
    }
}
