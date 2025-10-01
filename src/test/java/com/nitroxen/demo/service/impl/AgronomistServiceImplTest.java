package com.nitroxen.demo.service.impl;

import com.nitroxen.demo.dto.entity.AgronomistDTO;
import com.nitroxen.demo.dto.entity.AgronomistReportDTO;
import com.nitroxen.demo.entity.Agronomist;
import com.nitroxen.demo.entity.AgronomistReport;
import com.nitroxen.demo.entity.Farm;
import com.nitroxen.demo.entity.User;
import com.nitroxen.demo.enums.Role;
import com.nitroxen.demo.repository.AgronomistReportRepository;
import com.nitroxen.demo.repository.AgronomistRepository;
import com.nitroxen.demo.repository.FarmRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgronomistServiceImplTest {

    @Mock
    private AgronomistRepository agronomistRepository;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private AgronomistReportRepository reportRepository;

    @InjectMocks
    private AgronomistServiceImpl agronomistService;

    private Agronomist agronomist;
    private AgronomistDTO agronomistDTO;
    private Farm farm;
    private AgronomistReport report;
    private AgronomistReportDTO reportDTO;
    private final Long agronomistId = 1L;
    private final Long farmId = 1L;
    private final Long reportId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // Setup farm
        User owner = User.builder()
                .id(1L)
                .name("Test Owner")
                .role(Role.OWNER)
                .build();

        farm = Farm.builder()
                .id(farmId)
                .name("Test Farm")
                .location("Test Location")
                .totalArea(10000.0)
                .owner(owner)
                .build();

        // Setup agronomist
        agronomist = Agronomist.builder()
                .id(agronomistId)
                .name("Test Agronomist")
                .email("agronomist@test.com")
                .phone("+1234567890")
                .specialization("Hydroponics")
                .farms(new HashSet<>())
                .build();

        // Setup agronomist DTO
        agronomistDTO = AgronomistDTO.builder()
                .id(agronomistId)
                .name("Test Agronomist")
                .email("agronomist@test.com")
                .phone("+1234567890")
                .specialization("Hydroponics")
                .farmIds(new HashSet<>())
                .build();

        // Setup report
        report = AgronomistReport.builder()
                .id(reportId)
                .agronomist(agronomist)
                .farm(farm)
                .content("Test report content")
                .status("PENDING")
                .build();

        // Setup report DTO
        reportDTO = AgronomistReportDTO.builder()
                .id(reportId)
                .agronomistId(agronomistId)
                .farmId(farmId)
                .content("Test report content")
                .status("PENDING")
                .build();
    }

    @Test
    void createAgronomist_Success() {
        // Arrange
        when(agronomistRepository.save(any(Agronomist.class))).thenReturn(agronomist);

        // Act
        AgronomistDTO result = agronomistService.createAgronomist(agronomistDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Test Agronomist");
        assertThat(result.getEmail()).isEqualTo("agronomist@test.com");
        verify(agronomistRepository, times(1)).save(any(Agronomist.class));
    }

    @Test
    void updateAgronomist_Success() {
        // Arrange
        AgronomistDTO updateDTO = AgronomistDTO.builder()
                .name("Updated Agronomist")
                .email("updated@test.com")
                .phone("+9876543210")
                .specialization("Organic Farming")
                .build();

        Agronomist updatedAgronomist = Agronomist.builder()
                .id(agronomistId)
                .name("Updated Agronomist")
                .email("updated@test.com")
                .phone("+9876543210")
                .specialization("Organic Farming")
                .farms(new HashSet<>())
                .build();

        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.of(agronomist));
        when(agronomistRepository.save(any(Agronomist.class))).thenReturn(updatedAgronomist);

        // Act
        AgronomistDTO result = agronomistService.updateAgronomist(agronomistId, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Agronomist");
        assertThat(result.getEmail()).isEqualTo("updated@test.com");
        verify(agronomistRepository, times(1)).findById(agronomistId);
        verify(agronomistRepository, times(1)).save(any(Agronomist.class));
    }

    @Test
    void updateAgronomist_NotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> agronomistService.updateAgronomist(agronomistId, agronomistDTO));
        verify(agronomistRepository, never()).save(any(Agronomist.class));
    }

    @Test
    void deleteAgronomist_Success() {
        // Arrange
        doNothing().when(agronomistRepository).deleteById(agronomistId);

        // Act
        agronomistService.deleteAgronomist(agronomistId);

        // Assert
        verify(agronomistRepository, times(1)).deleteById(agronomistId);
    }

    @Test
    void getAllAgronomists_Success() {
        // Arrange
        List<Agronomist> agronomists = List.of(agronomist);
        when(agronomistRepository.findAll()).thenReturn(agronomists);

        // Act
        List<AgronomistDTO> results = agronomistService.getAllAgronomists();

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Test Agronomist");
        verify(agronomistRepository, times(1)).findAll();
    }

    @Test
    void getAgronomistById_Success() {
        // Arrange
        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.of(agronomist));

        // Act
        AgronomistDTO result = agronomistService.getAgronomistById(agronomistId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(agronomistId);
        assertThat(result.getName()).isEqualTo("Test Agronomist");
        verify(agronomistRepository, times(1)).findById(agronomistId);
    }

    @Test
    void getAgronomistById_NotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> agronomistService.getAgronomistById(agronomistId));
    }

    @Test
    void searchAgronomists_Success() {
        // Arrange
        String keyword = "Test";
        List<Agronomist> agronomists = List.of(agronomist);
        when(agronomistRepository.findByNameContainingIgnoreCase(keyword)).thenReturn(agronomists);

        // Act
        List<AgronomistDTO> results = agronomistService.searchAgronomists(keyword);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Test Agronomist");
        verify(agronomistRepository, times(1)).findByNameContainingIgnoreCase(keyword);
    }

    @Test
    void assignFarm_Success() {
        // Arrange
        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.of(agronomist));
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(agronomistRepository.save(any(Agronomist.class))).thenReturn(agronomist);

        // Act
        AgronomistDTO result = agronomistService.assignFarm(agronomistId, farmId);

        // Assert
        assertThat(result).isNotNull();
        verify(agronomistRepository, times(1)).findById(agronomistId);
        verify(farmRepository, times(1)).findById(farmId);
        verify(agronomistRepository, times(1)).save(any(Agronomist.class));
    }

    @Test
    void assignFarm_AgronomistNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> agronomistService.assignFarm(agronomistId, farmId));
        verify(farmRepository, never()).findById(anyLong());
    }

    @Test
    void assignFarm_FarmNotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.of(agronomist));
        when(farmRepository.findById(farmId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> agronomistService.assignFarm(agronomistId, farmId));
    }

    @Test
    void getAssignedFarms_Success() {
        // Arrange
        agronomist.getFarms().add(farm);
        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.of(agronomist));

        // Act
        Set<Long> result = agronomistService.getAssignedFarms(agronomistId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).contains(farmId);
        verify(agronomistRepository, times(1)).findById(agronomistId);
    }

    @Test
    void createReport_Success() {
        // Arrange
        when(agronomistRepository.findById(agronomistId)).thenReturn(Optional.of(agronomist));
        when(farmRepository.findById(farmId)).thenReturn(Optional.of(farm));
        when(reportRepository.save(any(AgronomistReport.class))).thenReturn(report);

        // Act
        AgronomistReportDTO result = agronomistService.createReport(agronomistId, reportDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Test report content");
        assertThat(result.getStatus()).isEqualTo("PENDING");
        verify(reportRepository, times(1)).save(any(AgronomistReport.class));
    }

    @Test
    void getReportsByAgronomist_Success() {
        // Arrange
        List<AgronomistReport> reports = List.of(report);
        when(reportRepository.findByAgronomistId(agronomistId)).thenReturn(reports);

        // Act
        List<AgronomistReportDTO> results = agronomistService.getReportsByAgronomist(agronomistId);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContent()).isEqualTo("Test report content");
        verify(reportRepository, times(1)).findByAgronomistId(agronomistId);
    }

    @Test
    void getReportsByFarm_Success() {
        // Arrange
        List<AgronomistReport> reports = List.of(report);
        when(reportRepository.findByFarmId(farmId)).thenReturn(reports);

        // Act
        List<AgronomistReportDTO> results = agronomistService.getReportsByFarm(farmId);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContent()).isEqualTo("Test report content");
        verify(reportRepository, times(1)).findByFarmId(farmId);
    }

    @Test
    void updateReport_Success() {
        // Arrange
        AgronomistReportDTO updateDTO = AgronomistReportDTO.builder()
                .content("Updated content")
                .status("COMPLETED")
                .build();

        AgronomistReport updatedReport = AgronomistReport.builder()
                .id(reportId)
                .agronomist(agronomist)
                .farm(farm)
                .content("Updated content")
                .status("COMPLETED")
                .build();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(AgronomistReport.class))).thenReturn(updatedReport);

        // Act
        AgronomistReportDTO result = agronomistService.updateReport(reportId, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Updated content");
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(reportRepository, times(1)).save(any(AgronomistReport.class));
    }

    @Test
    void updateReport_NotFound_ThrowsEntityNotFoundException() {
        // Arrange
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> agronomistService.updateReport(reportId, reportDTO));
        verify(reportRepository, never()).save(any(AgronomistReport.class));
    }

    @Test
    void deleteReport_Success() {
        // Arrange
        doNothing().when(reportRepository).deleteById(reportId);

        // Act
        agronomistService.deleteReport(reportId);

        // Assert
        verify(reportRepository, times(1)).deleteById(reportId);
    }
}
