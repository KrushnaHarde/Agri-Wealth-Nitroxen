package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.CreateUserRequest;
import com.nitroxen.demo.dto.response.UserResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Owner", description = "Farm owner management endpoints")
public class OwnerController {

    private final UserService userService;

    @PostMapping("/managers")
    @Operation(summary = "Create manager", description = "Create a new manager account")
    public ResponseEntity<UserResponse> createManager(@Valid @RequestBody CreateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long ownerId = ((User) authentication.getPrincipal()).getId();

        // Ensure the role is set to MANAGER
        request.setRole(Role.MANAGER);

        UserResponse response = userService.createUser(request, ownerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/workers")
    @Operation(summary = "Create worker", description = "Create a new worker account")
    public ResponseEntity<UserResponse> createWorker(@Valid @RequestBody CreateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long ownerId = ((User) authentication.getPrincipal()).getId();

        // Ensure the role is set to WORKER
        request.setRole(Role.WORKER);

        UserResponse response = userService.createUser(request, ownerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/managers")
    @Operation(summary = "Get all managers", description = "Retrieve all managers created by this owner")
    public ResponseEntity<List<UserResponse>> getAllManagers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long ownerId = ((User) authentication.getPrincipal()).getId();

        List<UserResponse> managers = userService.getUsersCreatedBy(ownerId, Role.MANAGER);
        return ResponseEntity.ok(managers);
    }

    @GetMapping("/workers")
    @Operation(summary = "Get all workers", description = "Retrieve all workers created by this owner")
    public ResponseEntity<List<UserResponse>> getAllWorkers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long ownerId = ((User) authentication.getPrincipal()).getId();

        List<UserResponse> workers = userService.getUsersCreatedBy(ownerId, Role.WORKER);
        return ResponseEntity.ok(workers);
    }
}
