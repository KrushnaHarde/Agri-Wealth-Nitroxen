package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.request.ZoneRequest;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.Polyhouse;
import com.nitroxen.demo.entity.Reservoir;
import com.nitroxen.demo.entity.Zone;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.PolyhouseRepository;
import com.nitroxen.demo.repository.ReservoirRepository;
import com.nitroxen.demo.repository.ZoneRepository;
import com.nitroxen.demo.service.ZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ZoneServiceImpl implements ZoneService {

    private final ZoneRepository zoneRepository;
    private final PolyhouseRepository polyhouseRepository;
    private final ReservoirRepository reservoirRepository;

    @Override
    @Transactional
    public ZoneResponse createZone(Long polyhouseId, ZoneRequest request, Long ownerId) {
        // Get polyhouse and verify ownership
        Polyhouse polyhouse = polyhouseRepository.findById(polyhouseId)
                .filter(p -> p.getFarm().getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Polyhouse not found with id: " + polyhouseId + " for owner id: " + ownerId));

        // Check if polyhouse already has the maximum number of zones (4)
        if (polyhouse.hasMaxZones()) {
            throw new ValidationException("Polyhouse already has the maximum number of zones (4)");
        }

        // Check if zone name is unique within this polyhouse
        if (zoneRepository.existsByNameAndPolyhouseId(request.getName(), polyhouseId)) {
            throw new ValidationException("Zone with this name already exists in this polyhouse");
        }

        // Find reservoir if specified
        Reservoir reservoir = null;
        if (request.getReservoirId() != null) {
            reservoir = reservoirRepository.findById(request.getReservoirId())
                    .filter(r -> r.getFarm().getId().equals(polyhouse.getFarm().getId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Reservoir not found with id: " + request.getReservoirId()));
        }

        // Create zone
        Zone zone = Zone.builder()
                .name(request.getName())
                .systemType(request.getSystemType())
                .cropType(request.getCropType())
                .cropVariety(request.getCropVariety())
                .plantingConfiguration(request.getPlantingConfiguration())
                .irrigationSetup(request.getIrrigationSetup())
                .dosingSystem(request.getDosingSystem())
                .polyhouse(polyhouse)
                .waterSource(reservoir)
                .build();

        // Save and return
        Zone savedZone = zoneRepository.save(zone);
        return mapToZoneResponse(savedZone);
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneResponse getZoneById(Long zoneId, Long ownerId) {
        Zone zone = getZoneByIdAndOwnerId(zoneId, ownerId);
        return mapToZoneResponse(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getZonesByPolyhouse(Long polyhouseId, Long ownerId) {
        // Verify polyhouse exists and ownership
        polyhouseRepository.findById(polyhouseId)
                .filter(p -> p.getFarm().getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Polyhouse not found with id: " + polyhouseId + " for owner id: " + ownerId));

        return zoneRepository.findByPolyhouseId(polyhouseId).stream()
                .map(this::mapToZoneResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ZoneResponse updateZone(Long zoneId, ZoneRequest request, Long ownerId) {
        Zone zone = getZoneByIdAndOwnerId(zoneId, ownerId);

        // Check if name is being changed and if new name is already taken
        if (!zone.getName().equals(request.getName()) &&
                zoneRepository.existsByNameAndPolyhouseId(request.getName(), zone.getPolyhouse().getId())) {
            throw new ValidationException("Zone with this name already exists in this polyhouse");
        }

        // Find reservoir if specified
        Reservoir reservoir = null;
        if (request.getReservoirId() != null) {
            reservoir = reservoirRepository.findById(request.getReservoirId())
                    .filter(r -> r.getFarm().getId().equals(zone.getPolyhouse().getFarm().getId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Reservoir not found with id: " + request.getReservoirId()));
        }

        // Update fields
        zone.setName(request.getName());
        zone.setSystemType(request.getSystemType());
        zone.setCropType(request.getCropType());
        zone.setCropVariety(request.getCropVariety());
        zone.setPlantingConfiguration(request.getPlantingConfiguration());
        zone.setIrrigationSetup(request.getIrrigationSetup());
        zone.setDosingSystem(request.getDosingSystem());
        zone.setWaterSource(reservoir);

        // Save and return
        Zone updatedZone = zoneRepository.save(zone);
        return mapToZoneResponse(updatedZone);
    }

    @Override
    @Transactional
    public void deleteZone(Long zoneId, Long ownerId) {
        Zone zone = getZoneByIdAndOwnerId(zoneId, ownerId);
        zoneRepository.delete(zone);
    }

    // Helper method to get zone by ID and verify owner
    private Zone getZoneByIdAndOwnerId(Long zoneId, Long ownerId) {
        return zoneRepository.findById(zoneId)
                .filter(zone -> zone.getPolyhouse().getFarm().getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId + " for owner id: " + ownerId));
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
