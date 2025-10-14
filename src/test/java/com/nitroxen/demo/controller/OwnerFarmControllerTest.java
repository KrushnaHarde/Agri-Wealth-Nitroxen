package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.FarmRequest;
import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.service.FarmService;
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
public class OwnerFarmControllerTest {

    @Mock
    private FarmService farmService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OwnerFarmController ownerFarmController;

    private User owner;
    private FarmRequest farmRequest;
    private FarmResponse farmResponse;
    private final Long ownerId = 1L;
    private final Long farmId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup owner
        owner = User.builder()
                .id(ownerId)
                .name("Test Owner")
                .build();

        // Setup farm request
        farmRequest = new FarmRequest();
        farmRequest.setName("Test Farm");
        farmRequest.setLocation("Test Location");
        farmRequest.setTotalArea(10000.0);
        farmRequest.setDescription("Test Description");

        // Setup farm response
        farmResponse = FarmResponse.builder()
                .id(farmId)
                .name("Test Farm")
                .location("Test Location")
                .totalArea(10000.0)
                .usedArea(0.0)
                .remainingArea(10000.0)
                .description("Test Description")
                .ownerId(ownerId)
                .ownerName("Test Owner")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Mock authentication to return the owner
        when(authentication.getPrincipal()).thenReturn(owner);
    }

    @Test
    void createFarm_Success() {
        // Arrange
        when(farmService.createFarm(any(FarmRequest.class), eq(ownerId))).thenReturn(farmResponse);

        // Act
        ResponseEntity<FarmResponse> response = ownerFarmController.createFarm(farmRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(farmResponse);
        verify(farmService, times(1)).createFarm(any(FarmRequest.class), eq(ownerId));
    }

    @Test
    void getAllFarms_Success() {
        // Arrange
        List<FarmResponse> farmResponses = List.of(farmResponse);
        when(farmService.getFarmsByOwner(ownerId)).thenReturn(farmResponses);

        // Act
        ResponseEntity<List<FarmResponse>> response = ownerFarmController.getAllFarms(authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(farmResponse);
        verify(farmService, times(1)).getFarmsByOwner(ownerId);
    }

    @Test
    void getFarmById_Success() {
        // Arrange
        when(farmService.getFarmById(farmId, ownerId)).thenReturn(farmResponse);

        // Act
        ResponseEntity<FarmResponse> response = ownerFarmController.getFarmById(farmId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(farmResponse);
        verify(farmService, times(1)).getFarmById(farmId, ownerId);
    }

    @Test
    void updateFarm_Success() {
        // Arrange
        FarmResponse updatedResponse = FarmResponse.builder()
                .id(farmId)
                .name("Updated Farm")
                .location("Updated Location")
                .totalArea(15000.0)
                .usedArea(0.0)
                .remainingArea(15000.0)
                .description("Updated Description")
                .ownerId(ownerId)
                .ownerName("Test Owner")
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(farmService.updateFarm(eq(farmId), any(FarmRequest.class), eq(ownerId))).thenReturn(updatedResponse);

        // Act
        ResponseEntity<FarmResponse> response = ownerFarmController.updateFarm(farmId, farmRequest, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedResponse);
        verify(farmService, times(1)).updateFarm(eq(farmId), any(FarmRequest.class), eq(ownerId));
    }

    @Test
    void deleteFarm_Success() {
        // Arrange
        doNothing().when(farmService).deleteFarm(farmId, ownerId);

        // Act
        ResponseEntity<Void> response = ownerFarmController.deleteFarm(farmId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(farmService, times(1)).deleteFarm(farmId, ownerId);
    }

    @Test
    void getRemainingArea_Success() {
        // Arrange
        Double remainingArea = 10000.0;
        when(farmService.getRemainingArea(farmId, ownerId)).thenReturn(remainingArea);

        // Act
        ResponseEntity<Double> response = ownerFarmController.getRemainingArea(farmId, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(remainingArea);
        verify(farmService, times(1)).getRemainingArea(farmId, ownerId);
    }
}
