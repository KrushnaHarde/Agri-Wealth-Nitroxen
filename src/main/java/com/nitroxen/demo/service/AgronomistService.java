package com.nitroxen.demo.service;

import com.nitroxen.demo.dto.entity.AgronomistDTO;
import com.nitroxen.demo.dto.entity.AgronomistReportDTO;
import java.util.List;
import java.util.Set;

public interface AgronomistService {
    AgronomistDTO createAgronomist(AgronomistDTO dto);
    AgronomistDTO updateAgronomist(Long id, AgronomistDTO dto);
    void deleteAgronomist(Long id);
    List<AgronomistDTO> getAllAgronomists();
    AgronomistDTO getAgronomistById(Long id);
    List<AgronomistDTO> searchAgronomists(String keyword);
    AgronomistDTO assignFarm(Long agronomistId, Long farmId);
    Set<Long> getAssignedFarms(Long agronomistId);
    AgronomistReportDTO createReport(Long agronomistId, AgronomistReportDTO dto);
    List<AgronomistReportDTO> getReportsByAgronomist(Long agronomistId);
    List<AgronomistReportDTO> getReportsByFarm(Long farmId);
    AgronomistReportDTO updateReport(Long reportId, AgronomistReportDTO dto);
    void deleteReport(Long reportId);
}