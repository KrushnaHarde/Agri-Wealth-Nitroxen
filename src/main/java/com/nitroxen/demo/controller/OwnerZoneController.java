package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.ZoneRequest;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.service.ZoneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Zone Management API", description = "Endpoints for zone management within polyhouses")
public class OwnerZoneController {

    private final ZoneService zoneService;

    @PostMapping("/polyhouses/{polyhouseId}/zones")
    @Operation(summary = "Create a new zone", description = "Creates a new zone in the specified polyhouse")
    public ResponseEntity<ZoneResponse> createZone(
            @PathVariable Long polyhouseId,
            @Valid @RequestBody ZoneRequest request,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        ZoneResponse createdZone = zoneService.createZone(polyhouseId, request, ownerId);
        return new ResponseEntity<>(createdZone, HttpStatus.CREATED);
    }

    @GetMapping("/polyhouses/{polyhouseId}/zones")
    @Operation(summary = "Get all zones in a polyhouse", description = "Returns a list of all zones in the specified polyhouse")
    public ResponseEntity<List<ZoneResponse>> getZonesByPolyhouse(
            @PathVariable Long polyhouseId,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        List<ZoneResponse> zones = zoneService.getZonesByPolyhouse(polyhouseId, ownerId);
        return ResponseEntity.ok(zones);
    }

    @GetMapping("/polyhouses/{polyhouseId}/zones/{zoneId}")
    @Operation(summary = "Get zone by ID", description = "Returns the zone with the specified ID in the specified polyhouse")
    public ResponseEntity<ZoneResponse> getZoneById(
            @PathVariable Long polyhouseId,
            @PathVariable Long zoneId,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        ZoneResponse zone = zoneService.getZoneById(zoneId, ownerId);
        return ResponseEntity.ok(zone);
    }

    @PutMapping("/zones/{id}")
    @Operation(summary = "Update a zone", description = "Updates the zone with the specified ID")
    public ResponseEntity<ZoneResponse> updateZone(
            @PathVariable Long id,
            @Valid @RequestBody ZoneRequest request,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        ZoneResponse updatedZone = zoneService.updateZone(id, request, ownerId);
        return ResponseEntity.ok(updatedZone);
    }

    @DeleteMapping("/zones/{id}")
    @Operation(summary = "Delete a zone", description = "Deletes the zone with the specified ID")
    public ResponseEntity<Void> deleteZone(
            @PathVariable Long id,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        zoneService.deleteZone(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    // Helper method to extract user ID from authentication
    private Long extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof com.nitroxen.demo.entity.User) {
            return ((com.nitroxen.demo.entity.User) authentication.getPrincipal()).getId();
        }
        throw new IllegalStateException("User not authenticated");
    }
}
