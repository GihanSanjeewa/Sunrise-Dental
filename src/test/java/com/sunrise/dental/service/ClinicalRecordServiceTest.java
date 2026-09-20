package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.ClinicalRecordRequest;
import com.sunrise.dental.dto.response.ClinicalRecordResponse;
import com.sunrise.dental.entity.ClinicalRecord;
import com.sunrise.dental.entity.Dentist;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.repository.ClinicalRecordRepository;
import com.sunrise.dental.repository.DentistRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.service.impl.ClinicalRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClinicalRecordServiceTest {

    @Mock
    private ClinicalRecordRepository clinicalRecordRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ClinicalRecordServiceImpl clinicalRecordService;

    private Patient patient;
    private Dentist dentist;
    private ClinicalRecordRequest request;

    @BeforeEach
    void setUp() {
        patient = new Patient("P-000001", "Nimal Perera", "Colombo", "0771112233", "nimal@test.com", LocalDate.of(1990, 1, 1), "MALE");
        patient.setId(1L);

        dentist = new Dentist("D-000001", "Dr. Samantha", "Orthodontics", "0779998877", "samantha@test.com", DentistStatus.AVAILABLE);
        dentist.setId(1L);

        request = new ClinicalRecordRequest();
        request.setPatientId(1L);
        request.setDentistId(1L);
        request.setVisitDate(LocalDate.now());
        request.setChiefComplaint("Toothache in lower left jaw");
        request.setClinicalNotes("Deep caries on tooth #36");
        request.setDiagnosis("Irreversible pulpitis on tooth #36");
        request.setTreatmentNotes("Recommended root canal therapy");
    }

    @Test
    @DisplayName("Successfully create clinical examination record with auto-generated record number")
    void shouldCreateClinicalRecordSuccessfully() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(clinicalRecordRepository.findMaxId()).thenReturn(0L);
        when(clinicalRecordRepository.existsByRecordNumber(anyString())).thenReturn(false);

        ClinicalRecord savedRecord = new ClinicalRecord(
                "CR-2026-000001",
                patient,
                dentist,
                null,
                request.getVisitDate(),
                request.getChiefComplaint(),
                null,
                null,
                null,
                request.getDiagnosis(),
                request.getClinicalNotes(),
                request.getTreatmentNotes(),
                null
        );
        savedRecord.setId(10L);

        when(clinicalRecordRepository.save(any(ClinicalRecord.class))).thenReturn(savedRecord);

        ClinicalRecordResponse response = clinicalRecordService.createClinicalRecord(request);

        assertNotNull(response);
        assertEquals("CR-2026-000001", response.getRecordNumber());
        assertEquals("Toothache in lower left jaw", response.getChiefComplaint());
        assertEquals(1L, response.getPatientId());
        assertEquals(1L, response.getDentistId());
        verify(clinicalRecordRepository, times(1)).save(any(ClinicalRecord.class));
        verify(auditService, times(1)).logAction(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Throw ResourceNotFoundException when patient does not exist")
    void shouldThrowExceptionWhenPatientNotFound() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            clinicalRecordService.createClinicalRecord(request);
        });

        verify(clinicalRecordRepository, never()).save(any(ClinicalRecord.class));
    }

    @Test
    @DisplayName("Retrieve clinical records for a patient ordered chronologically")
    void shouldGetClinicalRecordsByPatientId() {
        ClinicalRecord record = new ClinicalRecord(
                "CR-2026-000001", patient, dentist, null, LocalDate.now(),
                "Routine checkup", null, null, null, "Healthy", "Normal findings", null, null
        );
        record.setId(1L);

        when(clinicalRecordRepository.findByPatientIdOrderByVisitDateDescCreatedAtDesc(1L))
                .thenReturn(List.of(record));

        List<ClinicalRecordResponse> records = clinicalRecordService.getClinicalRecordsByPatientId(1L);

        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals("CR-2026-000001", records.get(0).getRecordNumber());
    }
}
