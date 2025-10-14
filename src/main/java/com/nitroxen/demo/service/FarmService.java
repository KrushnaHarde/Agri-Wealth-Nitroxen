package com.nitroxen.demo.service;

import com.nitroxen.demo.dto.request.FarmRequest;
import com.nitroxen.demo.dto.response.FarmResponse;

import java.util.List;

public interface FarmService {

    /**
     * Create a new farm
     * @param request Farm creation request
     * @param ownerId ID of the owner creating the farm
     * @return The created farm
     */
    FarmResponse createFarm(FarmRequest request, Long ownerId);

    /**
     * Get all farms owned by a specific user
     * @param ownerId ID of the farm owner
     * @return List of farms
     */
    List<FarmResponse> getFarmsByOwner(Long ownerId);

    /**
     * Get a farm by its ID
     * @param farmId ID of the farm to retrieve
     * @param ownerId ID of the owner (for access control)
     * @return The requested farm
     */
    FarmResponse getFarmById(Long farmId, Long ownerId);

    /**
     * Update an existing farm
     * @param farmId ID of the farm to update
     * @param request Updated farm data
     * @param ownerId ID of the owner (for access control)
     * @return The updated farm
     */
    FarmResponse updateFarm(Long farmId, FarmRequest request, Long ownerId);

    /**
     * Delete a farm
     * @param farmId ID of the farm to delete
     * @param ownerId ID of the owner (for access control)
     */
    void deleteFarm(Long farmId, Long ownerId);

    /**
     * Calculate the remaining area available in a farm
     * @param farmId ID of the farm
     * @param ownerId ID of the owner (for access control)
     * @return Remaining area in square meters
     */
    Double getRemainingArea(Long farmId, Long ownerId);
}
