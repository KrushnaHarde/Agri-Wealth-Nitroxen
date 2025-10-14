package com.nitroxen.demo.service;

import com.nitroxen.demo.dto.request.ReservoirRequest;
import com.nitroxen.demo.dto.response.ReservoirResponse;

import java.util.List;

public interface ReservoirService {

    /**
     * Create a new reservoir in a farm
     * @param farmId ID of the farm where the reservoir will be created
     * @param request Reservoir creation request
     * @param ownerId ID of the owner (for access control)
     * @return The created reservoir
     */
    ReservoirResponse createReservoir(Long farmId, ReservoirRequest request, Long ownerId);

    /**
     * Get all reservoirs in a farm
     * @param farmId ID of the farm
     * @param ownerId ID of the owner (for access control)
     * @return List of reservoirs
     */
    List<ReservoirResponse> getReservoirsByFarm(Long farmId, Long ownerId);

    /**
     * Get a reservoir by its ID
     * @param reservoirId ID of the reservoir to retrieve
     * @param ownerId ID of the owner (for access control)
     * @return The requested reservoir
     */
    ReservoirResponse getReservoirById(Long reservoirId, Long ownerId);

    /**
     * Update an existing reservoir
     * @param reservoirId ID of the reservoir to update
     * @param request Updated reservoir data
     * @param ownerId ID of the owner (for access control)
     * @return The updated reservoir
     */
    ReservoirResponse updateReservoir(Long reservoirId, ReservoirRequest request, Long ownerId);

    /**
     * Delete a reservoir
     * @param reservoirId ID of the reservoir to delete
     * @param ownerId ID of the owner (for access control)
     */
    void deleteReservoir(Long reservoirId, Long ownerId);
}
