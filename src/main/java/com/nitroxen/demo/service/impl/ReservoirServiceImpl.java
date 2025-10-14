package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.request.ReservoirRequest;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.Reservoir;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.FarmRepository;
import com.nitroxen.demo.repository.ReservoirRepository;
import com.nitroxen.demo.service.ReservoirService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservoirServiceImpl implements ReservoirService {

    private final ReservoirRepository reservoirRepository;
    private final FarmRepository farmRepository;

    @Override
    @Transactional
    public ReservoirResponse createReservoir(Long farmId, ReservoirRequest request, Long ownerId) {
        // Get farm and verify ownership
        Farm farm = farmRepository.findById(farmId)
                .filter(f -> f.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId + " for owner id: " + ownerId));

        // Check if reservoir name is unique within this farm
        if (reservoirRepository.existsByNameAndFarmId(request.getName(), farmId)) {
            throw new ValidationException("Reservoir with this name already exists in this farm");
        }

        // Create reservoir
        Reservoir reservoir = Reservoir.builder()
                .name(request.getName())
                .capacity(request.getCapacity())
                .waterSource(request.getWaterSource())
                .waterTreatment(request.getWaterTreatment())
                .farm(farm)
                .build();

        // Save and return
        Reservoir savedReservoir = reservoirRepository.save(reservoir);
        return mapToReservoirResponse(savedReservoir);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservoirResponse> getReservoirsByFarm(Long farmId, Long ownerId) {
        // Verify farm exists and ownership
        farmRepository.findById(farmId)
                .filter(farm -> farm.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId + " for owner id: " + ownerId));

        return reservoirRepository.findByFarmId(farmId).stream()
                .map(this::mapToReservoirResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservoirResponse getReservoirById(Long reservoirId, Long ownerId) {
        Reservoir reservoir = getReservoirByIdAndOwnerId(reservoirId, ownerId);
        return mapToReservoirResponse(reservoir);
    }

    @Override
    @Transactional
    public ReservoirResponse updateReservoir(Long reservoirId, ReservoirRequest request, Long ownerId) {
        Reservoir reservoir = getReservoirByIdAndOwnerId(reservoirId, ownerId);

        // Check if name is being changed and if new name is already taken
        if (!reservoir.getName().equals(request.getName()) &&
                reservoirRepository.existsByNameAndFarmId(request.getName(), reservoir.getFarm().getId())) {
            throw new ValidationException("Reservoir with this name already exists in this farm");
        }

        // Update fields
        reservoir.setName(request.getName());
        reservoir.setCapacity(request.getCapacity());
        reservoir.setWaterSource(request.getWaterSource());
        reservoir.setWaterTreatment(request.getWaterTreatment());

        // Save and return
        Reservoir updatedReservoir = reservoirRepository.save(reservoir);
        return mapToReservoirResponse(updatedReservoir);
    }

    @Override
    @Transactional
    public void deleteReservoir(Long reservoirId, Long ownerId) {
        Reservoir reservoir = getReservoirByIdAndOwnerId(reservoirId, ownerId);

        // Check if the reservoir is in use by any zones
        if (!reservoir.getServingZones().isEmpty()) {
            throw new ValidationException("Cannot delete reservoir as it is used by " + reservoir.getServingZones().size() + " zone(s)");
        }

        reservoirRepository.delete(reservoir);
    }

    // Helper method to get reservoir by ID and verify owner
    private Reservoir getReservoirByIdAndOwnerId(Long reservoirId, Long ownerId) {
        return reservoirRepository.findById(reservoirId)
                .filter(reservoir -> reservoir.getFarm().getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Reservoir not found with id: " + reservoirId + " for owner id: " + ownerId));
    }

    // Helper method to map Reservoir entity to ReservoirResponse DTO
    private ReservoirResponse mapToReservoirResponse(Reservoir reservoir) {
        return ReservoirResponse.builder()
                .id(reservoir.getId())
                .name(reservoir.getName())
                .capacity(reservoir.getCapacity())
                .waterSource(reservoir.getWaterSource())
                .waterTreatment(reservoir.getWaterTreatment())
                .farmId(reservoir.getFarm().getId())
                .farmName(reservoir.getFarm().getName())
                .servingZonesCount(reservoir.getServingZones().size())
                .createdAt(reservoir.getCreatedAt())
                .updatedAt(reservoir.getUpdatedAt())
                .build();
    }
}
