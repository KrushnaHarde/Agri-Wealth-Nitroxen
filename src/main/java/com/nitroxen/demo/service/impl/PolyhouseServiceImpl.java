package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.request.PolyhouseRequest;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.Polyhouse;
import com.nitroxen.demo.entity.Zone;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.FarmRepository;
import com.nitroxen.demo.repository.PolyhouseRepository;
import com.nitroxen.demo.repository.ZoneRepository;
import com.nitroxen.demo.service.PolyhouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolyhouseServiceImpl implements PolyhouseService {

    private final PolyhouseRepository polyhouseRepository;
    private final FarmRepository farmRepository;
    private final ZoneRepository zoneRepository;

    @Override
    @Transactional
    public PolyhouseResponse createPolyhouse(Long farmId, PolyhouseRequest request, Long ownerId) {
        // Get the farm and verify ownership
        Farm farm = farmRepository.findById(farmId)
                .filter(f -> f.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId + " for owner id: " + ownerId));

        // Check if the farm has enough area for this polyhouse
        if (farm.getRemainingArea() < request.getArea()) {
            throw new ValidationException("Not enough available area in farm. Available: " + farm.getRemainingArea() + " m², Requested: " + request.getArea() + " m²");
        }

        // Check if polyhouse name is unique within this farm
        if (polyhouseRepository.existsByNameAndFarmId(request.getName(), farmId)) {
            throw new ValidationException("Polyhouse with this name already exists in this farm");
        }

        // Create polyhouse
        Polyhouse polyhouse = Polyhouse.builder()
                .name(request.getName())
                .area(request.getArea())
                .type(request.getType())
                .specifications(request.getSpecifications())
                .equipment(request.getEquipment())
                .growingType(request.getGrowingType())
                .farm(farm)
                .build();

        // Save and return
        Polyhouse savedPolyhouse = polyhouseRepository.save(polyhouse);
        return mapToPolyhouseResponse(savedPolyhouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolyhouseResponse> getPolyhousesByFarm(Long farmId, Long ownerId) {
        // Verify farm exists and ownership
        farmRepository.findById(farmId)
                .filter(farm -> farm.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId + " for owner id: " + ownerId));

        return polyhouseRepository.findByFarmId(farmId).stream()
                .map(this::mapToPolyhouseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PolyhouseResponse getPolyhouseById(Long polyhouseId, Long ownerId) {
        Polyhouse polyhouse = getPolyhouseByIdAndOwnerId(polyhouseId, ownerId);
        return mapToPolyhouseResponse(polyhouse);
    }

    @Override
    @Transactional
    public PolyhouseResponse updatePolyhouse(Long polyhouseId, PolyhouseRequest request, Long ownerId) {
        Polyhouse polyhouse = getPolyhouseByIdAndOwnerId(polyhouseId, ownerId);
        Farm farm = polyhouse.getFarm();

        // Check if name is being changed and if new name is already taken
        if (!polyhouse.getName().equals(request.getName()) &&
                polyhouseRepository.existsByNameAndFarmId(request.getName(), farm.getId())) {
            throw new ValidationException("Polyhouse with this name already exists in this farm");
        }

        // If area is increasing, check if farm has enough space
        double areaDifference = request.getArea() - polyhouse.getArea();
        if (areaDifference > 0 && farm.getRemainingArea() < areaDifference) {
            throw new ValidationException("Not enough available area in farm. Available: " + farm.getRemainingArea() + " m², Additional needed: " + areaDifference + " m²");
        }

        // Update fields
        polyhouse.setName(request.getName());
        polyhouse.setArea(request.getArea());
        polyhouse.setType(request.getType());
        polyhouse.setSpecifications(request.getSpecifications());
        polyhouse.setEquipment(request.getEquipment());
        polyhouse.setGrowingType(request.getGrowingType());

        // Save and return
        Polyhouse updatedPolyhouse = polyhouseRepository.save(polyhouse);
        return mapToPolyhouseResponse(updatedPolyhouse);
    }

    @Override
    @Transactional
    public void deletePolyhouse(Long polyhouseId, Long ownerId) {
        Polyhouse polyhouse = getPolyhouseByIdAndOwnerId(polyhouseId, ownerId);
        polyhouseRepository.delete(polyhouse);
    }

    @Override
    @Transactional(readOnly = true)
    public PolyhouseResponse getPolyhouseWithZones(Long polyhouseId, Long ownerId) {
        Polyhouse polyhouse = getPolyhouseByIdAndOwnerId(polyhouseId, ownerId);
        // Zones are already loaded due to the fetch policy
        return mapToPolyhouseResponse(polyhouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getZonesByPolyhouse(Long polyhouseId, Long ownerId) {
        // Verify polyhouse exists and ownership
        getPolyhouseByIdAndOwnerId(polyhouseId, ownerId);

        return zoneRepository.findByPolyhouseId(polyhouseId).stream()
                .map(this::mapToZoneResponse)
                .collect(Collectors.toList());
    }

    // Helper method to get polyhouse by ID and verify owner
    private Polyhouse getPolyhouseByIdAndOwnerId(Long polyhouseId, Long ownerId) {
        return polyhouseRepository.findById(polyhouseId)
                .filter(polyhouse -> polyhouse.getFarm().getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Polyhouse not found with id: " + polyhouseId + " for owner id: " + ownerId));
    }

    // Helper method to map Polyhouse entity to PolyhouseResponse DTO
    private PolyhouseResponse mapToPolyhouseResponse(Polyhouse polyhouse) {
        return PolyhouseResponse.builder()
                .id(polyhouse.getId())
                .name(polyhouse.getName())
                .area(polyhouse.getArea())
                .type(polyhouse.getType())
                .specifications(polyhouse.getSpecifications())
                .equipment(polyhouse.getEquipment())
                .growingType(polyhouse.getGrowingType())
                .farmId(polyhouse.getFarm().getId())
                .farmName(polyhouse.getFarm().getName())
                .zoneCount(polyhouse.getZones().size())
                .createdAt(polyhouse.getCreatedAt())
                .updatedAt(polyhouse.getUpdatedAt())
                .build();
    }

    // Helper method to map Zone entity to ZoneResponse DTO
    private ZoneResponse mapToZoneResponse(Zone zone) {
        return ZoneResponse.builder()
                .id(zone.getId())
                .name(zone.getName())
                .systemType(zone.getSystemType())
                .cropType(zone.getCropType())
                .cropVariety(zone.getCropVariety())
                .plantingConfiguration(zone.getPlantingConfiguration())
                .irrigationSetup(zone.getIrrigationSetup())
                .dosingSystem(zone.getDosingSystem())
                .polyhouseId(zone.getPolyhouse().getId())
                .polyhouseName(zone.getPolyhouse().getName())
                .waterSourceId(zone.getWaterSource() != null ? zone.getWaterSource().getId() : null)
                .waterSourceName(zone.getWaterSource() != null ? zone.getWaterSource().getName() : null)
                .createdAt(zone.getCreatedAt())
                .updatedAt(zone.getUpdatedAt())
                .build();
    }
}
