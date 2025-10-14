package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.request.FarmRequest;
import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.FarmRepository;
import com.nitroxen.demo.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FarmServiceImplTest {

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FarmServiceImpl farmService;

    private User owner;
    private Farm farm;
    private FarmRequest farmRequest;
    private final Long ownerId = 1L;
    private final Long farmId = 1L;
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

        // Setup farm request
        farmRequest = new FarmRequest();
        farmRequest.setName("Test Farm");
        farmRequest.setLocation("Test Location");
        farmRequest.setTotalArea(10000.0);
        farmRequest.setDescription("Test Description");
    }

    @Test
    void createFarm_Success() {
        // Arrange
        when(farmRepository.existsByNameAndOwnerId("Test Farm", ownerId)).thenReturn(false);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(farmRepository.save(any(Farm.class))).thenReturn(farm);

        // Act
        FarmResponse response = farmService.createFarm(farmRequest, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(farmId);
        assertThat(response.getName()).isEqualTo("Test Farm");
        assertThat(response.getTotalArea()).isEqualTo(10000.0);
        assertThat(response.getOwnerId()).isEqualTo(ownerId);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    void createFarm_DuplicateName_ThrowsValidationException() {
        // Arrange
        when(farmRepository.existsByNameAndOwnerId("Test Farm", ownerId)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> farmService.createFarm(farmRequest, ownerId));
        verify(farmRepository, never()).save(any(Farm.class));
    }

    @Test
    void createFarm_OwnerNotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(farmRepository.existsByNameAndOwnerId("Test Farm", ownerId)).thenReturn(false);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> farmService.createFarm(farmRequest, ownerId));
        verify(farmRepository, never()).save(any(Farm.class));
    }

    @Test
    void getFarmsByOwner_ReturnsListOfFarms() {
        // Arrange
        List<Farm> farms = List.of(farm);
        when(farmRepository.findByOwnerId(ownerId)).thenReturn(farms);

        // Act
        List<FarmResponse> responses = farmService.getFarmsByOwner(ownerId);

        // Assert
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(farmId);
        assertThat(responses.get(0).getName()).isEqualTo("Test Farm");
        verify(farmRepository, times(1)).findByOwnerId(ownerId);
    }

    @Test
    void getFarmById_Success() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));

        // Act
        FarmResponse response = farmService.getFarmById(farmId, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(farmId);
        assertThat(response.getName()).isEqualTo("Test Farm");
        verify(farmRepository, times(1)).findById(farmId);
    }

    @Test
    void getFarmById_NotFound_ThrowsResourceNotFoundException() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> farmService.getFarmById(farmId, ownerId));
    }

    @Test
    void getFarmById_NotOwned_ThrowsResourceNotFoundException() {
        // Arrange
        Long differentOwnerId = 2L;
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> farmService.getFarmById(farmId, differentOwnerId));
    }

    @Test
    void updateFarm_Success() {
        // Arrange
        FarmRequest updateRequest = new FarmRequest();
        updateRequest.setName("Updated Farm");
        updateRequest.setLocation("Updated Location");
        updateRequest.setTotalArea(15000.0);
        updateRequest.setDescription("Updated Description");

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(farmRepository.existsByNameAndOwnerId("Updated Farm", ownerId)).thenReturn(false);

        Farm updatedFarm = Farm.builder()
                .id(farmId)
                .name("Updated Farm")
                .location("Updated Location")
                .totalArea(15000.0)
                .description("Updated Description")
                .owner(owner)
                .polyhouses(new ArrayList<>())
                .reservoirs(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

        when(farmRepository.save(any(Farm.class))).thenReturn(updatedFarm);

        // Act
        FarmResponse response = farmService.updateFarm(farmId, updateRequest, ownerId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Updated Farm");
        assertThat(response.getTotalArea()).isEqualTo(15000.0);
        verify(farmRepository, times(1)).save(any(Farm.class));
    }

    @Test
    void updateFarm_DuplicateName_ThrowsValidationException() {
        // Arrange
        FarmRequest updateRequest = new FarmRequest();
        updateRequest.setName("Other Farm");
        updateRequest.setLocation("Test Location");
        updateRequest.setTotalArea(10000.0);
        updateRequest.setDescription("Test Description");

        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(farmRepository.existsByNameAndOwnerId("Other Farm", ownerId)).thenReturn(true);

        // Act & Assert
        assertThrows(ValidationException.class, () -> farmService.updateFarm(farmId, updateRequest, ownerId));
        verify(farmRepository, never()).save(any(Farm.class));
    }

    @Test
    void deleteFarm_Success() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        doNothing().when(farmRepository).delete(farm);

        // Act
        farmService.deleteFarm(farmId, ownerId);

        // Assert
        verify(farmRepository, times(1)).delete(farm);
    }

    @Test
    void getRemainingArea_Success() {
        // Arrange
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));

        // Act
        Double remainingArea = farmService.getRemainingArea(farmId, ownerId);

        // Assert
        assertThat(remainingArea).isEqualTo(10000.0); // No polyhouses, so full area is available
    }
}
