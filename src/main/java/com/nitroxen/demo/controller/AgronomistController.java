package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.entity.AgronomistDTO;
import com.nitroxen.demo.dto.entity.AgronomistReportDTO;
import com.nitroxen.demo.service.AgronomistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/agronomists")
@RequiredArgsConstructor
public class AgronomistController {

    private final AgronomistService agronomistService;

    @PostMapping
    public ResponseEntity<AgronomistDTO> createAgronomist(@RequestBody AgronomistDTO agronomistDTO) {
        return new ResponseEntity<>(agronomistService.createAgronomist(agronomistDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AgronomistDTO>> getAllAgronomists() {
        return ResponseEntity.ok(agronomistService.getAllAgronomists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgronomistDTO> getAgronomistById(@PathVariable Long id) {
        return ResponseEntity.ok(agronomistService.getAgronomistById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgronomistDTO> updateAgronomist(
            @PathVariable Long id,
            @RequestBody AgronomistDTO agronomistDTO) {
        return ResponseEntity.ok(agronomistService.updateAgronomist(id, agronomistDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAgronomist(@PathVariable Long id) {
        agronomistService.deleteAgronomist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AgronomistDTO>> searchAgronomists(@RequestParam String keyword) {
        return ResponseEntity.ok(agronomistService.searchAgronomists(keyword));
    }

    @PostMapping("/{id}/assign-farm/{farmId}")
    public ResponseEntity<AgronomistDTO> assignFarm(
            @PathVariable Long id,
            @PathVariable Long farmId) {
        return ResponseEntity.ok(agronomistService.assignFarm(id, farmId));
    }

    @GetMapping("/{id}/farms")
    public ResponseEntity<Set<Long>> getAssignedFarms(@PathVariable Long id) {
        return ResponseEntity.ok(agronomistService.getAssignedFarms(id));
    }

    @PostMapping("/{id}/reports")
    public ResponseEntity<AgronomistReportDTO> createReport(
            @PathVariable Long id,
            @RequestBody AgronomistReportDTO reportDTO) {
        return new ResponseEntity<>(agronomistService.createReport(id, reportDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/reports")
    public ResponseEntity<List<AgronomistReportDTO>> getReportsByAgronomist(@PathVariable Long id) {
        return ResponseEntity.ok(agronomistService.getReportsByAgronomist(id));
    }

    @GetMapping("/farms/{farmId}/reports")
    public ResponseEntity<List<AgronomistReportDTO>> getReportsByFarm(@PathVariable Long farmId) {
        return ResponseEntity.ok(agronomistService.getReportsByFarm(farmId));
    }

    @PutMapping("/reports/{reportId}")
    public ResponseEntity<AgronomistReportDTO> updateReport(
            @PathVariable Long reportId,
            @RequestBody AgronomistReportDTO reportDTO) {
        return ResponseEntity.ok(agronomistService.updateReport(reportId, reportDTO));
    }

    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long reportId) {
        agronomistService.deleteReport(reportId);
        return ResponseEntity.noContent().build();
    }
}