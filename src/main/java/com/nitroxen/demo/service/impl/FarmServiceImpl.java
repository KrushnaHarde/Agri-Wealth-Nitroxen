package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.request.FarmRequest;
import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.FarmRepository;
import com.nitroxen.demo.repository.UserRepository;
import com.nitroxen.demo.service.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FarmResponse createFarm(FarmRequest request, Long ownerId) {
        // Validate farm name uniqueness for this owner
        if (farmRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
            throw new ValidationException("Farm with this name already exists for this owner");
        }

        // Get owner
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));

        // Create farm
        Farm farm = Farm.builder()
                .name(request.getName())
                .location(request.getLocation())
                .totalArea(request.getTotalArea())
                .description(request.getDescription())
                .owner(owner)
                .build();

        // Save and return
        Farm savedFarm = farmRepository.save(farm);
        return mapToFarmResponse(savedFarm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmResponse> getFarmsByOwner(Long ownerId) {
        return farmRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToFarmResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FarmResponse getFarmById(Long farmId, Long ownerId) {
        Farm farm = getFarmByIdAndOwnerId(farmId, ownerId);
        return mapToFarmResponse(farm);
    }

    @Override
    @Transactional
    public FarmResponse updateFarm(Long farmId, FarmRequest request, Long ownerId) {
        Farm farm = getFarmByIdAndOwnerId(farmId, ownerId);

        // Check if name is being changed and if new name is already taken
        if (!farm.getName().equals(request.getName()) &&
                farmRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
            throw new ValidationException("Farm with this name already exists for this owner");
        }

        // Update fields
        farm.setName(request.getName());
        farm.setLocation(request.getLocation());
        farm.setTotalArea(request.getTotalArea());
        farm.setDescription(request.getDescription());

        // Save and return
        Farm updatedFarm = farmRepository.save(farm);
        return mapToFarmResponse(updatedFarm);
    }

    @Override
    @Transactional
    public void deleteFarm(Long farmId, Long ownerId) {
        Farm farm = getFarmByIdAndOwnerId(farmId, ownerId);
        farmRepository.delete(farm);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getRemainingArea(Long farmId, Long ownerId) {
        Farm farm = getFarmByIdAndOwnerId(farmId, ownerId);
        return farm.getRemainingArea();
    }

    // Helper method to get farm by ID and owner ID
    private Farm getFarmByIdAndOwnerId(Long farmId, Long ownerId) {
        return farmRepository.findById(farmId)
                .filter(farm -> farm.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId + " for owner id: " + ownerId));
    }

    // Helper method to map Farm entity to FarmResponse DTO
    private FarmResponse mapToFarmResponse(Farm farm) {
        return FarmResponse.builder()
                .id(farm.getId())
                .name(farm.getName())
                .location(farm.getLocation())
                .totalArea(farm.getTotalArea())
                .usedArea(farm.getUsedArea())
                .remainingArea(farm.getRemainingArea())
                .description(farm.getDescription())
                .ownerId(farm.getOwner().getId())
                .ownerName(farm.getOwner().getName())
                .createdAt(farm.getCreatedAt())
                .updatedAt(farm.getUpdatedAt())
                .build();
    }
}
