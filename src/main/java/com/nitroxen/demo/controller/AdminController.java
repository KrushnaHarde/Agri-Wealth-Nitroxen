package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.CreateUserRequest;
import com.nitroxen.demo.dto.response.UserResponse;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.service.UserService;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin API", description = "Endpoints for farm owner management by admin")
public class AdminController {

    private final UserService userService;

    @PostMapping("/owners")
    @Operation(summary = "Create a new farm owner", description = "Creates a new farm owner with subscription")
    public ResponseEntity<UserResponse> createOwner(@Valid @RequestBody CreateUserRequest request, Authentication authentication) {
        // Set role to OWNER
        request.setRole(Role.OWNER);

        // Get admin ID from authentication
        Long adminId = extractUserId(authentication);

        // Create owner
        UserResponse createdOwner = userService.createUser(request, adminId);
        return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
    }

    @GetMapping("/owners")
    @Operation(summary = "Get all farm owners", description = "Returns a list of all farm owners")
    public ResponseEntity<List<UserResponse>> getAllOwners() {
        List<UserResponse> owners = userService.getUsersByRole(Role.OWNER);
        return ResponseEntity.ok(owners);
    }

    @GetMapping("/owners/{id}")
    @Operation(summary = "Get farm owner by ID", description = "Returns the farm owner with the specified ID")
    public ResponseEntity<UserResponse> getOwnerById(@PathVariable Long id) {
        // Implementation depends on UserService having a method to get user by ID
        // This is just a placeholder for now
        return ResponseEntity.ok().build();
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue tracking data", description = "Returns revenue tracking data for all farm owners")
    public ResponseEntity<?> getRevenueTracking() {
        // This is a placeholder for revenue tracking
        // Implement actual revenue tracking logic in a future update
        return ResponseEntity.ok().build();
    }

    // Helper method to extract user ID from authentication
    private Long extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof com.nitroxen.demo.entity.User) {
            return ((com.nitroxen.demo.entity.User) authentication.getPrincipal()).getId();
        }
        throw new IllegalStateException("User not authenticated");
    }
}
