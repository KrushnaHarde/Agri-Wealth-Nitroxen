package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.Polyhouse;
import com.nitroxen.demo.entity.Reservoir;
import com.nitroxen.demo.entity.Zone;
import com.nitroxen.demo.exception.ResourceNotFoundException;
import com.nitroxen.demo.exception.ValidationException;
import com.nitroxen.demo.repository.*;
import com.nitroxen.demo.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final FarmRepository farmRepository;
    private final PolyhouseRepository polyhouseRepository;
    private final ZoneRepository zoneRepository;
    private final ReservoirRepository reservoirRepository;
    private final FarmAssignmentRepository farmAssignmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FarmResponse> getAssignedFarms(Long managerId) {
        // Get all farms assigned to the manager
        List<Long> assignedFarmIds = farmAssignmentRepository.findAssignedFarmIdsByManagerId(managerId);
        List<Farm> farms = farmRepository.findAllById(assignedFarmIds);

        return farms.stream()
                .map(this::mapToFarmResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FarmResponse getAssignedFarmById(Long farmId, Long managerId) {
        // Verify farm is assigned to manager
        if (!isFarmAssignedToManager(farmId, managerId)) {
            throw new ValidationException("Farm not assigned to this manager");
        }

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        return mapToFarmResponse(farm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolyhouseResponse> getPolyhousesByAssignedFarm(Long farmId, Long managerId) {
        // Verify farm is assigned to manager
        if (!isFarmAssignedToManager(farmId, managerId)) {
            throw new ValidationException("Farm not assigned to this manager");
        }

        List<Polyhouse> polyhouses = polyhouseRepository.findByFarmId(farmId);

        return polyhouses.stream()
                .map(this::mapToPolyhouseResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PolyhouseResponse getPolyhouseById(Long polyhouseId, Long managerId) {
        Polyhouse polyhouse = polyhouseRepository.findById(polyhouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Polyhouse not found with id: " + polyhouseId));

        // Verify farm is assigned to manager
        if (!isFarmAssignedToManager(polyhouse.getFarm().getId(), managerId)) {
            throw new ValidationException("Polyhouse not in a farm assigned to this manager");
        }

        return mapToPolyhouseResponse(polyhouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getZonesByPolyhouse(Long polyhouseId, Long managerId) {
        Polyhouse polyhouse = polyhouseRepository.findById(polyhouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Polyhouse not found with id: " + polyhouseId));

        // Verify farm is assigned to manager
        if (!isFarmAssignedToManager(polyhouse.getFarm().getId(), managerId)) {
            throw new ValidationException("Polyhouse not in a farm assigned to this manager");
        }

        List<Zone> zones = zoneRepository.findByPolyhouseId(polyhouseId);

        return zones.stream()
                .map(this::mapToZoneResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneResponse getZoneById(Long zoneId, Long managerId) {
        Zone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with id: " + zoneId));

        // Verify farm is assigned to manager
        if (!isFarmAssignedToManager(zone.getPolyhouse().getFarm().getId(), managerId)) {
            throw new ValidationException("Zone not in a farm assigned to this manager");
        }

        return mapToZoneResponse(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservoirResponse> getReservoirsByFarm(Long farmId, Long managerId) {
        // Verify farm is assigned to manager
        if (!isFarmAssignedToManager(farmId, managerId)) {
            throw new ValidationException("Farm not assigned to this manager");
        }

        List<Reservoir> reservoirs = reservoirRepository.findByFarmId(farmId);

        return reservoirs.stream()
                .map(this::mapToReservoirResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFarmAssignedToManager(Long farmId, Long managerId) {
        return farmAssignmentRepository.existsByFarmIdAndManagerIdAndActiveTrue(farmId, managerId);
    }

    // Mapping methods
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
