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
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Manager", description = "Manager endpoints for worker management")
public class ManagerController {

    private final UserService userService;

    @PostMapping("/workers")
    @Operation(summary = "Create worker", description = "Create a new worker account assigned to this manager")
    public ResponseEntity<UserResponse> createWorker(@Valid @RequestBody CreateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        // Ensure the role is set to WORKER
        request.setRole(Role.WORKER);

        UserResponse response = userService.createUser(request, managerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/workers")
    @Operation(summary = "View assigned workers", description = "Retrieve all workers assigned to this manager")
    public ResponseEntity<List<UserResponse>> getAssignedWorkers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        List<UserResponse> workers = userService.getUsersCreatedBy(managerId, Role.WORKER);
        return ResponseEntity.ok(workers);
    }
}
