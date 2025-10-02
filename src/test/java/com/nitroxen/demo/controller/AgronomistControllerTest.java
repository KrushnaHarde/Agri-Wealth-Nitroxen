package com.nitroxen.demo.controller;

import com.nitroxen.demo.dto.entity.AgronomistDTO;
import com.nitroxen.demo.dto.entity.AgronomistReportDTO;
import com.nitroxen.demo.service.AgronomistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AgronomistControllerTest {

    @Mock
    private AgronomistService agronomistService;

    @InjectMocks
    private AgronomistController agronomistController;

    private AgronomistDTO agronomistDTO;
    private AgronomistReportDTO reportDTO;
    private final Long agronomistId = 1L;
    private final Long farmId = 1L;
    private final Long reportId = 1L;

    @BeforeEach
    void setUp() {
        // Setup agronomist DTO
        agronomistDTO = AgronomistDTO.builder()
                .id(agronomistId)
                .name("Test Agronomist")
                .email("agronomist@test.com")
                .phone("+1234567890")
                .specialization("Hydroponics")
                .farmIds(new HashSet<>())
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
        when(agronomistService.createAgronomist(any(AgronomistDTO.class))).thenReturn(agronomistDTO);

        // Act
        ResponseEntity<AgronomistDTO> response = agronomistController.createAgronomist(agronomistDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(agronomistDTO);
        verify(agronomistService, times(1)).createAgronomist(any(AgronomistDTO.class));
    }

    @Test
    void getAllAgronomists_Success() {
        // Arrange
        List<AgronomistDTO> agronomists = List.of(agronomistDTO);
        when(agronomistService.getAllAgronomists()).thenReturn(agronomists);

        // Act
        ResponseEntity<List<AgronomistDTO>> response = agronomistController.getAllAgronomists();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(agronomistDTO);
        verify(agronomistService, times(1)).getAllAgronomists();
    }

    @Test
    void getAgronomistById_Success() {
        // Arrange
        when(agronomistService.getAgronomistById(agronomistId)).thenReturn(agronomistDTO);

        // Act
        ResponseEntity<AgronomistDTO> response = agronomistController.getAgronomistById(agronomistId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(agronomistDTO);
        verify(agronomistService, times(1)).getAgronomistById(agronomistId);
    }

    @Test
    void updateAgronomist_Success() {
        // Arrange
        AgronomistDTO updatedDTO = AgronomistDTO.builder()
                .id(agronomistId)
                .name("Updated Agronomist")
                .email("updated@test.com")
                .phone("+9876543210")
                .specialization("Organic Farming")
                .farmIds(new HashSet<>())
                .build();

        when(agronomistService.updateAgronomist(eq(agronomistId), any(AgronomistDTO.class))).thenReturn(updatedDTO);

        // Act
        ResponseEntity<AgronomistDTO> response = agronomistController.updateAgronomist(agronomistId, updatedDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedDTO);
        assertThat(response.getBody().getName()).isEqualTo("Updated Agronomist");
        verify(agronomistService, times(1)).updateAgronomist(eq(agronomistId), any(AgronomistDTO.class));
    }

    @Test
    void deleteAgronomist_Success() {
        // Arrange
        doNothing().when(agronomistService).deleteAgronomist(agronomistId);

        // Act
        ResponseEntity<Void> response = agronomistController.deleteAgronomist(agronomistId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(agronomistService, times(1)).deleteAgronomist(agronomistId);
    }

    @Test
    void searchAgronomists_Success() {
        // Arrange
        String keyword = "Test";
        List<AgronomistDTO> agronomists = List.of(agronomistDTO);
        when(agronomistService.searchAgronomists(keyword)).thenReturn(agronomists);

        // Act
        ResponseEntity<List<AgronomistDTO>> response = agronomistController.searchAgronomists(keyword);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(agronomistService, times(1)).searchAgronomists(keyword);
    }

    @Test
    void assignFarm_Success() {
        // Arrange
        Set<Long> farmIds = new HashSet<>();
        farmIds.add(farmId);
        AgronomistDTO assignedDTO = AgronomistDTO.builder()
                .id(agronomistId)
                .name("Test Agronomist")
                .email("agronomist@test.com")
                .phone("+1234567890")
                .specialization("Hydroponics")
                .farmIds(farmIds)
                .build();

        when(agronomistService.assignFarm(agronomistId, farmId)).thenReturn(assignedDTO);

        // Act
        ResponseEntity<AgronomistDTO> response = agronomistController.assignFarm(agronomistId, farmId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(assignedDTO);
        assertThat(response.getBody().getFarmIds()).contains(farmId);
        verify(agronomistService, times(1)).assignFarm(agronomistId, farmId);
    }

    @Test
    void getAssignedFarms_Success() {
        // Arrange
        Set<Long> farmIds = Set.of(farmId);
        when(agronomistService.getAssignedFarms(agronomistId)).thenReturn(farmIds);

        // Act
        ResponseEntity<Set<Long>> response = agronomistController.getAssignedFarms(agronomistId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()).contains(farmId);
        verify(agronomistService, times(1)).getAssignedFarms(agronomistId);
    }

    @Test
    void createReport_Success() {
        // Arrange
        when(agronomistService.createReport(eq(agronomistId), any(AgronomistReportDTO.class))).thenReturn(reportDTO);

        // Act
        ResponseEntity<AgronomistReportDTO> response = agronomistController.createReport(agronomistId, reportDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(reportDTO);
        verify(agronomistService, times(1)).createReport(eq(agronomistId), any(AgronomistReportDTO.class));
    }

    @Test
    void getReportsByAgronomist_Success() {
        // Arrange
        List<AgronomistReportDTO> reports = List.of(reportDTO);
        when(agronomistService.getReportsByAgronomist(agronomistId)).thenReturn(reports);

        // Act
        ResponseEntity<List<AgronomistReportDTO>> response = agronomistController.getReportsByAgronomist(agronomistId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(reportDTO);
        verify(agronomistService, times(1)).getReportsByAgronomist(agronomistId);
    }

    @Test
    void getReportsByFarm_Success() {
        // Arrange
        List<AgronomistReportDTO> reports = List.of(reportDTO);
        when(agronomistService.getReportsByFarm(farmId)).thenReturn(reports);

        // Act
        ResponseEntity<List<AgronomistReportDTO>> response = agronomistController.getReportsByFarm(farmId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0)).isEqualTo(reportDTO);
        verify(agronomistService, times(1)).getReportsByFarm(farmId);
    }

    @Test
    void updateReport_Success() {
        // Arrange
        AgronomistReportDTO updatedDTO = AgronomistReportDTO.builder()
                .id(reportId)
                .agronomistId(agronomistId)
                .farmId(farmId)
                .content("Updated content")
                .status("COMPLETED")
                .build();

        when(agronomistService.updateReport(eq(reportId), any(AgronomistReportDTO.class))).thenReturn(updatedDTO);

        // Act
        ResponseEntity<AgronomistReportDTO> response = agronomistController.updateReport(reportId, updatedDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedDTO);
        assertThat(response.getBody().getStatus()).isEqualTo("COMPLETED");
        verify(agronomistService, times(1)).updateReport(eq(reportId), any(AgronomistReportDTO.class));
    }

    @Test
    void deleteReport_Success() {
        // Arrange
        doNothing().when(agronomistService).deleteReport(reportId);

        // Act
        ResponseEntity<Void> response = agronomistController.deleteReport(reportId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(agronomistService, times(1)).deleteReport(reportId);
    }
}
