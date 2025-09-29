package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.request.CreateUserRequest;
import com.nitroxen.demo.dto.response.FarmResponse;
import com.nitroxen.demo.dto.response.PolyhouseResponse;
import com.nitroxen.demo.dto.response.ReservoirResponse;
import com.nitroxen.demo.dto.response.UserResponse;
import com.nitroxen.demo.dto.response.ZoneResponse;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.service.ManagerService;
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
    private final ManagerService managerService;

    // Worker Management Endpoints

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

    // Farm Visibility Endpoints

    @GetMapping("/farms")
    @Operation(summary = "View assigned farms", description = "Retrieve all farms assigned to this manager")
    public ResponseEntity<List<FarmResponse>> getAssignedFarms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        List<FarmResponse> farms = managerService.getAssignedFarms(managerId);
        return ResponseEntity.ok(farms);
    }

    @GetMapping("/farms/{id}")
    @Operation(summary = "View specific assigned farm", description = "Retrieve details of a specific farm assigned to this manager")
    public ResponseEntity<FarmResponse> getAssignedFarmById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        FarmResponse farm = managerService.getAssignedFarmById(id, managerId);
        return ResponseEntity.ok(farm);
    }

    // Polyhouse Visibility Endpoints

    @GetMapping("/farms/{id}/polyhouses")
    @Operation(summary = "View polyhouses in assigned farm", description = "Retrieve all polyhouses in a farm assigned to this manager")
    public ResponseEntity<List<PolyhouseResponse>> getPolyhousesByFarm(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        List<PolyhouseResponse> polyhouses = managerService.getPolyhousesByAssignedFarm(id, managerId);
        return ResponseEntity.ok(polyhouses);
    }

    @GetMapping("/polyhouses/{id}")
    @Operation(summary = "View specific polyhouse", description = "Retrieve details of a specific polyhouse in an assigned farm")
    public ResponseEntity<PolyhouseResponse> getPolyhouseById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        PolyhouseResponse polyhouse = managerService.getPolyhouseById(id, managerId);
        return ResponseEntity.ok(polyhouse);
    }

    // Zone Visibility Endpoints

    @GetMapping("/polyhouses/{id}/zones")
    @Operation(summary = "View zones in polyhouse", description = "Retrieve all zones in a polyhouse within an assigned farm")
    public ResponseEntity<List<ZoneResponse>> getZonesByPolyhouse(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        List<ZoneResponse> zones = managerService.getZonesByPolyhouse(id, managerId);
        return ResponseEntity.ok(zones);
    }

    @GetMapping("/zones/{id}")
    @Operation(summary = "View specific zone", description = "Retrieve details of a specific zone in an assigned farm's polyhouse")
    public ResponseEntity<ZoneResponse> getZoneById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        ZoneResponse zone = managerService.getZoneById(id, managerId);
        return ResponseEntity.ok(zone);
    }

    // Reservoir Visibility Endpoints

    @GetMapping("/farms/{id}/reservoirs")
    @Operation(summary = "View reservoirs in farm", description = "Retrieve all reservoirs in a farm assigned to this manager")
    public ResponseEntity<List<ReservoirResponse>> getReservoirsByFarm(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long managerId = ((User) authentication.getPrincipal()).getId();

        List<ReservoirResponse> reservoirs = managerService.getReservoirsByFarm(id, managerId);
        return ResponseEntity.ok(reservoirs);
    }
}
