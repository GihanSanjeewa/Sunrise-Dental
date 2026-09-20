package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.ToothRecordRequest;
import com.sunrise.dental.dto.response.DentalChartResponse;
import com.sunrise.dental.dto.response.ToothRecordResponse;
import com.sunrise.dental.entity.DentalToothRecord;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.entity.ToothHistory;
import com.sunrise.dental.enums.ToothCondition;
import com.sunrise.dental.repository.DentalToothRecordRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.repository.ToothHistoryRepository;
import com.sunrise.dental.service.impl.DentalChartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DentalChartServiceTest {

    @Mock
    private DentalToothRecordRepository toothRecordRepository;

    @Mock
    private ToothHistoryRepository toothHistoryRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private DentalChartServiceImpl dentalChartService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient("P-000001", "Sunil Perera", "Colombo", "0771234567", "sunil@test.com", LocalDate.of(1985, 3, 15), "MALE");
        patient.setId(1L);
    }

    @Test
    @DisplayName("Auto-initialize 32 FDI permanent teeth if patient chart does not exist")
    void shouldInitializePatientChartWhenEmpty() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(toothRecordRepository.existsByPatientIdAndToothNumber(eq(1L), anyInt())).thenReturn(false);

        dentalChartService.initializePatientChart(1L);

        // 32 adult permanent teeth should be saved
        verify(toothRecordRepository, times(32)).save(any(DentalToothRecord.class));
    }

    @Test
    @DisplayName("Update tooth condition and verify history entry is recorded")
    void shouldUpdateToothConditionAndLogHistory() {
        DentalToothRecord existingTooth = new DentalToothRecord(patient, 16, ToothCondition.HEALTHY, "SOUND", "Sound", null);
        existingTooth.setId(100L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(toothRecordRepository.findByPatientIdAndToothNumber(1L, 16)).thenReturn(Optional.of(existingTooth));
        when(toothRecordRepository.save(any(DentalToothRecord.class))).thenReturn(existingTooth);

        ToothRecordRequest updateRequest = new ToothRecordRequest();
        updateRequest.setPatientId(1L);
        updateRequest.setToothNumber(16);
        updateRequest.setCondition(ToothCondition.CARIES);
        updateRequest.setNotes("Occlusal pit caries detected");

        ToothRecordResponse response = dentalChartService.updateToothCondition(updateRequest);

        assertNotNull(response);
        assertEquals(ToothCondition.CARIES, response.getCondition());
        assertEquals("Occlusal pit caries detected", response.getNotes());
        verify(toothHistoryRepository, times(1)).save(any(ToothHistory.class));
        verify(auditService, times(1)).logAction(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Retrieve dental chart correctly partitioned into 4 quadrants")
    void shouldGetDentalChartPartitionedIntoQuadrants() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        List<DentalToothRecord> allTeeth = new ArrayList<>();
        // Q1: 18-11
        for (int i = 18; i >= 11; i--) {
            allTeeth.add(new DentalToothRecord(patient, i, ToothCondition.HEALTHY, "SOUND", "Tooth " + i, null));
        }
        // Q2: 21-28
        for (int i = 21; i <= 28; i++) {
            allTeeth.add(new DentalToothRecord(patient, i, ToothCondition.HEALTHY, "SOUND", "Tooth " + i, null));
        }
        // Q3: 31-38
        for (int i = 31; i <= 38; i++) {
            allTeeth.add(new DentalToothRecord(patient, i, ToothCondition.HEALTHY, "SOUND", "Tooth " + i, null));
        }
        // Q4: 48-41
        for (int i = 48; i >= 41; i--) {
            allTeeth.add(new DentalToothRecord(patient, i, ToothCondition.HEALTHY, "SOUND", "Tooth " + i, null));
        }

        when(toothRecordRepository.findByPatientIdOrderByToothNumberAsc(1L)).thenReturn(allTeeth);

        DentalChartResponse chart = dentalChartService.getDentalChartByPatientId(1L);

        assertNotNull(chart);
        assertEquals(8, chart.getUpperRight().size());
        assertEquals(8, chart.getUpperLeft().size());
        assertEquals(8, chart.getLowerLeft().size());
        assertEquals(8, chart.getLowerRight().size());
        assertEquals(32, chart.getAllTeeth().size());
    }
}
