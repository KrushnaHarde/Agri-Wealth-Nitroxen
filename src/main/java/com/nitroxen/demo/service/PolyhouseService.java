package com.nitroxen.demo.service;

import com.nitroxen.demo.dto.request.PolyhouseRequest;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;

import java.util.List;

public interface PolyhouseService {

    /**
     * Create a new polyhouse in a farm
     * @param farmId ID of the farm where the polyhouse will be created
     * @param request Polyhouse creation request
     * @param ownerId ID of the owner (for access control)
     * @return The created polyhouse
     */
    PolyhouseResponse createPolyhouse(Long farmId, PolyhouseRequest request, Long ownerId);

    /**
     * Get all polyhouses in a farm
     * @param farmId ID of the farm
     * @param ownerId ID of the owner (for access control)
     * @return List of polyhouses
     */
    List<PolyhouseResponse> getPolyhousesByFarm(Long farmId, Long ownerId);

    /**
     * Get a polyhouse by its ID
     * @param polyhouseId ID of the polyhouse to retrieve
     * @param ownerId ID of the owner (for access control)
     * @return The requested polyhouse
     */
    PolyhouseResponse getPolyhouseById(Long polyhouseId, Long ownerId);

    /**
     * Update an existing polyhouse
     * @param polyhouseId ID of the polyhouse to update
     * @param request Updated polyhouse data
     * @param ownerId ID of the owner (for access control)
     * @return The updated polyhouse
     */
    PolyhouseResponse updatePolyhouse(Long polyhouseId, PolyhouseRequest request, Long ownerId);

    /**
     * Delete a polyhouse
     * @param polyhouseId ID of the polyhouse to delete
     * @param ownerId ID of the owner (for access control)
     */
    void deletePolyhouse(Long polyhouseId, Long ownerId);

    /**
     * Get a polyhouse with its zones
     * @param polyhouseId ID of the polyhouse
     * @param ownerId ID of the owner (for access control)
     * @return The polyhouse with its zones
     */
    PolyhouseResponse getPolyhouseWithZones(Long polyhouseId, Long ownerId);

    /**
     * Get all zones in a polyhouse
     * @param polyhouseId ID of the polyhouse
     * @param ownerId ID of the owner (for access control)
     * @return List of zones in the polyhouse
     */
    List<ZoneResponse> getZonesByPolyhouse(Long polyhouseId, Long ownerId);
}
