package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.FarmRequest;
import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.service.FarmService;
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
@RequestMapping("/api/owner/farms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Farm Management API", description = "Endpoints for farm management by farm owners")
public class OwnerFarmController {

    private final FarmService farmService;

    @PostMapping
    @Operation(summary = "Create a new farm", description = "Creates a new farm for the authenticated owner")
    public ResponseEntity<FarmResponse> createFarm(@Valid @RequestBody FarmRequest request, Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        FarmResponse createdFarm = farmService.createFarm(request, ownerId);
        return new ResponseEntity<>(createdFarm, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all farms", description = "Returns a list of all farms owned by the authenticated user")
    public ResponseEntity<List<FarmResponse>> getAllFarms(Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        List<FarmResponse> farms = farmService.getFarmsByOwner(ownerId);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get farm by ID", description = "Returns the farm with the specified ID if owned by the authenticated user")
    public ResponseEntity<FarmResponse> getFarmById(@PathVariable Long id, Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        FarmResponse farm = farmService.getFarmById(id, ownerId);
        return ResponseEntity.ok(farm);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a farm", description = "Updates the farm with the specified ID if owned by the authenticated user")
    public ResponseEntity<FarmResponse> updateFarm(@PathVariable Long id, @Valid @RequestBody FarmRequest request, Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        FarmResponse updatedFarm = farmService.updateFarm(id, request, ownerId);
        return ResponseEntity.ok(updatedFarm);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a farm", description = "Deletes the farm with the specified ID if owned by the authenticated user")
    public ResponseEntity<Void> deleteFarm(@PathVariable Long id, Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        farmService.deleteFarm(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/remaining-area")
    @Operation(summary = "Get remaining area", description = "Returns the remaining available area in the farm with the specified ID")
    public ResponseEntity<Double> getRemainingArea(@PathVariable Long id, Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        Double remainingArea = farmService.getRemainingArea(id, ownerId);
        return ResponseEntity.ok(remainingArea);
    }

    // Helper method to extract user ID from authentication
    private Long extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof com.nitroxen.demo.entity.User) {
            return ((com.nitroxen.demo.entity.User) authentication.getPrincipal()).getId();
        }
        throw new IllegalStateException("User not authenticated");
    }
}
