package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.PolyhouseRequest;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.service.PolyhouseService;
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
@Tag(name = "Polyhouse Management API", description = "Endpoints for polyhouse management by farm owners")
public class OwnerPolyhouseController {

    private final PolyhouseService polyhouseService;

    @PostMapping("/farms/{farmId}/polyhouses")
    @Operation(summary = "Create a new polyhouse", description = "Creates a new polyhouse in the specified farm")
    public ResponseEntity<PolyhouseResponse> createPolyhouse(
            @PathVariable Long farmId,
            @Valid @RequestBody PolyhouseRequest request,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        PolyhouseResponse createdPolyhouse = polyhouseService.createPolyhouse(farmId, request, ownerId);
        return new ResponseEntity<>(createdPolyhouse, HttpStatus.CREATED);
    }

    @GetMapping("/farms/{farmId}/polyhouses")
    @Operation(summary = "Get all polyhouses in a farm", description = "Returns a list of all polyhouses in the specified farm")
    public ResponseEntity<List<PolyhouseResponse>> getPolyhousesByFarm(
            @PathVariable Long farmId,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        List<PolyhouseResponse> polyhouses = polyhouseService.getPolyhousesByFarm(farmId, ownerId);
        return ResponseEntity.ok(polyhouses);
    }

    @GetMapping("/polyhouses/{id}")
    @Operation(summary = "Get polyhouse by ID", description = "Returns the polyhouse with the specified ID")
    public ResponseEntity<PolyhouseResponse> getPolyhouseById(
            @PathVariable Long id,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        PolyhouseResponse polyhouse = polyhouseService.getPolyhouseById(id, ownerId);
        return ResponseEntity.ok(polyhouse);
    }

    @PutMapping("/polyhouses/{id}")
    @Operation(summary = "Update a polyhouse", description = "Updates the polyhouse with the specified ID")
    public ResponseEntity<PolyhouseResponse> updatePolyhouse(
            @PathVariable Long id,
            @Valid @RequestBody PolyhouseRequest request,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        PolyhouseResponse updatedPolyhouse = polyhouseService.updatePolyhouse(id, request, ownerId);
        return ResponseEntity.ok(updatedPolyhouse);
    }

    @DeleteMapping("/polyhouses/{id}")
    @Operation(summary = "Delete a polyhouse", description = "Deletes the polyhouse with the specified ID")
    public ResponseEntity<Void> deletePolyhouse(
            @PathVariable Long id,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        polyhouseService.deletePolyhouse(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/polyhouses/{id}/with-zones")
    @Operation(summary = "Get polyhouse with zones", description = "Returns the polyhouse with the specified ID including its zones")
    public ResponseEntity<PolyhouseResponse> getPolyhouseWithZones(
            @PathVariable Long id,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        PolyhouseResponse polyhouse = polyhouseService.getPolyhouseWithZones(id, ownerId);
        return ResponseEntity.ok(polyhouse);
    }

    @GetMapping("/polyhouses/{id}/zones")
    @Operation(summary = "Get zones in a polyhouse", description = "Returns a list of all zones in the specified polyhouse")
    public ResponseEntity<List<ZoneResponse>> getZonesByPolyhouse(
            @PathVariable Long id,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        List<ZoneResponse> zones = polyhouseService.getZonesByPolyhouse(id, ownerId);
        return ResponseEntity.ok(zones);
    }

    // Helper method to extract user ID from authentication
    private Long extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof com.nitroxen.demo.entity.User) {
            return ((com.nitroxen.demo.entity.User) authentication.getPrincipal()).getId();
        }
        throw new IllegalStateException("User not authenticated");
    }
}
