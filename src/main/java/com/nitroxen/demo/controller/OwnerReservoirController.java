package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.ReservoirRequest;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.service.ReservoirService;
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
@Tag(name = "Reservoir Management API", description = "Endpoints for reservoir management within farms")
public class OwnerReservoirController {

    private final ReservoirService reservoirService;

    @PostMapping("/farms/{farmId}/reservoirs")
    @Operation(summary = "Create a new reservoir", description = "Creates a new water reservoir in the specified farm")
    public ResponseEntity<ReservoirResponse> createReservoir(
            @PathVariable Long farmId,
            @Valid @RequestBody ReservoirRequest request,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        ReservoirResponse createdReservoir = reservoirService.createReservoir(farmId, request, ownerId);
        return new ResponseEntity<>(createdReservoir, HttpStatus.CREATED);
    }

    @GetMapping("/farms/{farmId}/reservoirs")
    @Operation(summary = "Get all reservoirs in a farm", description = "Returns a list of all reservoirs in the specified farm")
    public ResponseEntity<List<ReservoirResponse>> getReservoirsByFarm(
            @PathVariable Long farmId,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        List<ReservoirResponse> reservoirs = reservoirService.getReservoirsByFarm(farmId, ownerId);
        return ResponseEntity.ok(reservoirs);
    }

    @GetMapping("/reservoirs/{id}")
    @Operation(summary = "Get reservoir by ID", description = "Returns the reservoir with the specified ID")
    public ResponseEntity<ReservoirResponse> getReservoirById(
            @PathVariable Long id,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        ReservoirResponse reservoir = reservoirService.getReservoirById(id, ownerId);
        return ResponseEntity.ok(reservoir);
    }

    @PutMapping("/reservoirs/{id}")
    @Operation(summary = "Update a reservoir", description = "Updates the reservoir with the specified ID")
    public ResponseEntity<ReservoirResponse> updateReservoir(
            @PathVariable Long id,
            @Valid @RequestBody ReservoirRequest request,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        ReservoirResponse updatedReservoir = reservoirService.updateReservoir(id, request, ownerId);
        return ResponseEntity.ok(updatedReservoir);
    }

    @DeleteMapping("/reservoirs/{id}")
    @Operation(summary = "Delete a reservoir", description = "Deletes the reservoir with the specified ID if it's not used by any zones")
    public ResponseEntity<Void> deleteReservoir(
            @PathVariable Long id,
            Authentication authentication) {
        Long ownerId = extractUserId(authentication);
        reservoirService.deleteReservoir(id, ownerId);
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
