package com.nitroxen.demo.service;

import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;

import java.util.List;

/**
 * Service interface for manager-specific operations
 */
public interface ManagerService {

    /**
     * Get all farms assigned to a manager
     * @param managerId ID of the manager
     * @return List of farms assigned to the manager
     */
    List<FarmResponse> getAssignedFarms(Long managerId);

    /**
     * Get a specific farm assigned to a manager
     * @param farmId ID of the farm
     * @param managerId ID of the manager
     * @return The requested farm if assigned to the manager
     */
    FarmResponse getAssignedFarmById(Long farmId, Long managerId);

    /**
     * Get all polyhouses in a farm assigned to a manager
     * @param farmId ID of the farm
     * @param managerId ID of the manager
     * @return List of polyhouses in the farm
     */
    List<PolyhouseResponse> getPolyhousesByAssignedFarm(Long farmId, Long managerId);

    /**
     * Get a specific polyhouse in a farm assigned to a manager
     * @param polyhouseId ID of the polyhouse
     * @param managerId ID of the manager
     * @return The requested polyhouse if in an assigned farm
     */
    PolyhouseResponse getPolyhouseById(Long polyhouseId, Long managerId);

    /**
     * Get all zones in a polyhouse within a farm assigned to a manager
     * @param polyhouseId ID of the polyhouse
     * @param managerId ID of the manager
     * @return List of zones in the polyhouse
     */
    List<ZoneResponse> getZonesByPolyhouse(Long polyhouseId, Long managerId);

    /**
     * Get a specific zone in a polyhouse within a farm assigned to a manager
     * @param zoneId ID of the zone
     * @param managerId ID of the manager
     * @return The requested zone if in an assigned farm's polyhouse
     */
    ZoneResponse getZoneById(Long zoneId, Long managerId);

    /**
     * Get all reservoirs in a farm assigned to a manager
     * @param farmId ID of the farm
     * @param managerId ID of the manager
     * @return List of reservoirs in the farm
     */
    List<ReservoirResponse> getReservoirsByFarm(Long farmId, Long managerId);

    /**
     * Check if a farm is assigned to a manager
     * @param farmId ID of the farm
     * @param managerId ID of the manager
     * @return true if the farm is assigned to the manager, false otherwise
     */
    boolean isFarmAssignedToManager(Long farmId, Long managerId);
}
