package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.ReservoirRequest;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.service.ReservoirService;
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
public class OwnerReservoirControllerTest {

    @Mock
    private ReservoirService reservoirService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OwnerReservoirController ownerReservoirController;

    private User owner;
    private ReservoirRequest reservoirRequest;
    private ReservoirResponse reservoirResponse;

    private final Long ownerId = 1L;
    private final Long farmId = 1L;
    private final Long reservoirId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup owner
        owner = User.builder()
                .id(ownerId)
                .name("Test Owner")
                .build();

        // Setup reservoir request
        reservoirRequest = new ReservoirRequest();
        reservoirRequest.setName("Test Reservoir");
        reservoirRequest.setCapacity(5000.0);
        reservoirRequest.setWaterSource("Municipal");
        reservoirRequest.setWaterTreatment("Filtration");

        // Setup reservoir response
        reservoirResponse = ReservoirResponse.builder()
                .id(reservoirId)
                .name("Test Reservoir")
                .capacity(5000.0)
                .waterSource("Municipal")
                .waterTreatment("Filtration")
                .farmId(farmId)
                .farmName("Test Farm")
                .servingZonesCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Mock authentication to return the owner
        when(authentication.getPrincipal()).thenReturn(owner);
    }

    @Test
    void createReservoir_Success() {
        // Arrange
        when(reservoirService.createReservoir(eq(farmId), any(ReservoirRequest.class), eq(ownerId))).thenReturn(reservoirResponse);

        // Act
        ResponseEntity<ReservoirResponse> response = ownerReservoirController.createReservoir(farmId, reservoirRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(reservoirResponse);
        verify(reservoirService, times(1)).createReservoir(eq(farmId), any(ReservoirRequest.class), eq(ownerId));
    }

    @Test
    void getReservoirsByFarm_Success() {
        // Arrange
        List<ReservoirResponse> reservoirResponses = List.of(reservoirResponse);
        when(reservoirService.getReservoirsByFarm(farmId, ownerId)).thenReturn(reservoirResponses);

        // Act
        ResponseEntity<List<ReservoirResponse>> response = ownerReservoirController.getReservoirsByFarm(farmId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(reservoirResponse);
        verify(reservoirService, times(1)).getReservoirsByFarm(farmId, ownerId);
    }

    @Test
    void getReservoirById_Success() {
        // Arrange
        when(reservoirService.getReservoirById(reservoirId, ownerId)).thenReturn(reservoirResponse);

        // Act
        ResponseEntity<ReservoirResponse> response = ownerReservoirController.getReservoirById(reservoirId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(reservoirResponse);
        verify(reservoirService, times(1)).getReservoirById(reservoirId, ownerId);
    }

    @Test
    void updateReservoir_Success() {
        // Arrange
        ReservoirResponse updatedResponse = ReservoirResponse.builder()
                .id(reservoirId)
                .name("Updated Reservoir")
                .capacity(7000.0)
                .waterSource("Well Water")
                .waterTreatment("RO System")
                .farmId(farmId)
                .farmName("Test Farm")
                .servingZonesCount(0)
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(reservoirService.updateReservoir(eq(reservoirId), any(ReservoirRequest.class), eq(ownerId))).thenReturn(updatedResponse);

        // Act
        ResponseEntity<ReservoirResponse> response = ownerReservoirController.updateReservoir(reservoirId, reservoirRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedResponse);
        verify(reservoirService, times(1)).updateReservoir(eq(reservoirId), any(ReservoirRequest.class), eq(ownerId));
    }

    @Test
    void deleteReservoir_Success() {
        // Arrange
        doNothing().when(reservoirService).deleteReservoir(reservoirId, ownerId);

        // Act
        ResponseEntity<Void> response = ownerReservoirController.deleteReservoir(reservoirId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(reservoirService, times(1)).deleteReservoir(reservoirId, ownerId);
    }
}
