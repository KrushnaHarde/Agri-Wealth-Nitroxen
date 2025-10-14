package com.nitroxen.demo.service;

import com.nitroxen.demo.dto.request.ZoneRequest;
import com.nitroxen.demo.dto.response.ZoneResponse;

import java.util.List;

public interface ZoneService {

    /**
     * Create a new zone in a polyhouse
     * @param polyhouseId ID of the polyhouse where the zone will be created
     * @param request Zone creation request
     * @param ownerId ID of the owner (for access control)
     * @return The created zone
     */
    ZoneResponse createZone(Long polyhouseId, ZoneRequest request, Long ownerId);

    /**
     * Get a zone by its ID
     * @param zoneId ID of the zone to retrieve
     * @param ownerId ID of the owner (for access control)
     * @return The requested zone
     */
    ZoneResponse getZoneById(Long zoneId, Long ownerId);

    /**
     * Get all zones in a polyhouse
     * @param polyhouseId ID of the polyhouse
     * @param ownerId ID of the owner (for access control)
     * @return List of zones
     */
    List<ZoneResponse> getZonesByPolyhouse(Long polyhouseId, Long ownerId);

    /**
     * Update an existing zone
     * @param zoneId ID of the zone to update
     * @param request Updated zone data
     * @param ownerId ID of the owner (for access control)
     * @return The updated zone
     */
    ZoneResponse updateZone(Long zoneId, ZoneRequest request, Long ownerId);

    /**
     * Delete a zone
     * @param zoneId ID of the zone to delete
     * @param ownerId ID of the owner (for access control)
     */
    void deleteZone(Long zoneId, Long ownerId);
}
