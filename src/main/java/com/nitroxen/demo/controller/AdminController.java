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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

    private final UserService userService;

    @PostMapping("/owners")
    @Operation(summary = "Create farm owner", description = "Create a new farm owner account")
    public ResponseEntity<UserResponse> createOwner(@Valid @RequestBody CreateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long adminId = ((com.nitroxen.demo.entity.User) authentication.getPrincipal()).getId();

        // Ensure the role is set to OWNER
        request.setRole(Role.OWNER);

        UserResponse response = userService.createUser(request, adminId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owners")
    @Operation(summary = "Get all farm owners", description = "Retrieve all farm owner accounts")
    public ResponseEntity<List<UserResponse>> getAllOwners() {
        List<UserResponse> owners = userService.getUsersByRole(Role.OWNER);
        return ResponseEntity.ok(owners);
    }
}
