package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.entity.AgronomistDTO;
import com.nitroxen.demo.dto.entity.AgronomistReportDTO;
import com.nitroxen.demo.entity.Agronomist;
import com.nitroxen.demo.entity.AgronomistReport;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.repository.AgronomistRepository;
import com.nitroxen.demo.repository.AgronomistReportRepository;
import com.nitroxen.demo.repository.FarmRepository;
import com.nitroxen.demo.service.AgronomistService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AgronomistServiceImpl implements AgronomistService {

    private final AgronomistRepository agronomistRepository;
    private final FarmRepository farmRepository;
    private final AgronomistReportRepository reportRepository;

    @Override
    public AgronomistDTO createAgronomist(AgronomistDTO dto) {
        Agronomist agronomist = convertToEntity(dto);
        return convertToDto(agronomistRepository.save(agronomist));
    }

    @Override
    public AgronomistDTO updateAgronomist(Long id, AgronomistDTO dto) {
        Agronomist agronomist = agronomistRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Agronomist not found"));
        
        updateEntityFromDto(agronomist, dto);
        return convertToDto(agronomistRepository.save(agronomist));
    }

    @Override
    public void deleteAgronomist(Long id) {
        agronomistRepository.deleteById(id);
    }

    @Override
    public List<AgronomistDTO> getAllAgronomists() {
        return agronomistRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public AgronomistDTO getAgronomistById(Long id) {
        return agronomistRepository.findById(id)
            .map(this::convertToDto)
            .orElseThrow(() -> new EntityNotFoundException("Agronomist not found"));
    }

    @Override
    public List<AgronomistDTO> searchAgronomists(String keyword) {
        return agronomistRepository.findByNameContainingIgnoreCase(keyword).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Override
    public AgronomistDTO assignFarm(Long agronomistId, Long farmId) {
        Agronomist agronomist = agronomistRepository.findById(agronomistId)
            .orElseThrow(() -> new EntityNotFoundException("Agronomist not found"));
        Farm farm = farmRepository.findById(farmId)
            .orElseThrow(() -> new EntityNotFoundException("Farm not found"));
        
        agronomist.getFarms().add(farm);
        return convertToDto(agronomistRepository.save(agronomist));
    }

    @Override
    public Set<Long> getAssignedFarms(Long agronomistId) {
        Agronomist agronomist = agronomistRepository.findById(agronomistId)
            .orElseThrow(() -> new EntityNotFoundException("Agronomist not found"));
        return agronomist.getFarms().stream()
            .map(Farm::getId)
            .collect(Collectors.toSet());
    }

    @Override
    public AgronomistReportDTO createReport(Long agronomistId, AgronomistReportDTO dto) {
        Agronomist agronomist = agronomistRepository.findById(agronomistId)
            .orElseThrow(() -> new EntityNotFoundException("Agronomist not found"));
        Farm farm = farmRepository.findById(dto.getFarmId())
            .orElseThrow(() -> new EntityNotFoundException("Farm not found"));

        AgronomistReport report = AgronomistReport.builder()
            .agronomist(agronomist)
            .farm(farm)
            .content(dto.getContent())
            .status(dto.getStatus())
            .build();

        return convertToReportDto(reportRepository.save(report));
    }

    @Override
    public List<AgronomistReportDTO> getReportsByAgronomist(Long agronomistId) {
        return reportRepository.findByAgronomistId(agronomistId).stream()
            .map(this::convertToReportDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<AgronomistReportDTO> getReportsByFarm(Long farmId) {
        return reportRepository.findByFarmId(farmId).stream()
            .map(this::convertToReportDto)
            .collect(Collectors.toList());
    }

    @Override
    public AgronomistReportDTO updateReport(Long reportId, AgronomistReportDTO dto) {
        AgronomistReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> new EntityNotFoundException("Report not found"));
        
        report.setContent(dto.getContent());
        report.setStatus(dto.getStatus());
        
        return convertToReportDto(reportRepository.save(report));
    }

    @Override
    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }

    private Agronomist convertToEntity(AgronomistDTO dto) {
        return Agronomist.builder()
            .id(dto.getId())
            .name(dto.getName())
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .specialization(dto.getSpecialization())
            .build();
    }

    private AgronomistDTO convertToDto(Agronomist entity) {
        return AgronomistDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .email(entity.getEmail())
            .phone(entity.getPhone())
            .specialization(entity.getSpecialization())
            .farmIds(entity.getFarms().stream()
                .map(Farm::getId)
                .collect(Collectors.toSet()))
            .build();
    }

    private void updateEntityFromDto(Agronomist entity, AgronomistDTO dto) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setSpecialization(dto.getSpecialization());
    }

    private AgronomistReportDTO convertToReportDto(AgronomistReport report) {
        return AgronomistReportDTO.builder()
            .id(report.getId())
            .agronomistId(report.getAgronomist().getId())
            .farmId(report.getFarm().getId())
            .content(report.getContent())
            .status(report.getStatus())
            .build();
    }
}