package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.PatientRequest;
import com.sunrise.dental.dto.response.PatientResponse;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.exception.DuplicateResourceException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PatientServiceImpl patientService;

    private PatientRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new PatientRequest(
                "Kamal Silva",
                "No. 10, Kandy Road, Kiribathgoda",
                "0712345678",
                "kamal.silva@gmail.com",
                LocalDate.of(1988, 5, 20),
                "MALE"
        );
    }

    @Test
    @DisplayName("TC004: Successfully register new patient with auto-generated patient number")
    void shouldRegisterPatientSuccessfully() {
        when(patientRepository.existsByContactNumber("0712345678")).thenReturn(false);
        when(patientRepository.existsByEmail("kamal.silva@gmail.com")).thenReturn(false);
        when(patientRepository.findMaxId()).thenReturn(5L);
        when(patientRepository.existsByPatientNumber(anyString())).thenReturn(false);

        Patient saved = new Patient("P-000006", "Kamal Silva", "No. 10, Kandy Road, Kiribathgoda",
                "0712345678", "kamal.silva@gmail.com", LocalDate.of(1988, 5, 20), "MALE");
        saved.setId(6L);
        when(patientRepository.save(any(Patient.class))).thenReturn(saved);

        PatientResponse response = patientService.registerPatient(validRequest);

        assertNotNull(response);
        assertEquals("P-000006", response.getPatientNumber());
        assertEquals("Kamal Silva", response.getFullName());
        verify(patientRepository, times(1)).save(any(Patient.class));
    }

    @Test
    @DisplayName("TC003: Reject patient registration with blank name")
    void shouldRejectEmptyPatientName() {
        validRequest.setFullName("");

        assertThrows(ValidationException.class, () -> {
            patientService.registerPatient(validRequest);
        });

        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("TC006: Reject duplicate patient with identical contact number")
    void shouldRejectDuplicatePatientContactNumber() {
        when(patientRepository.existsByContactNumber("0712345678")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> {
            patientService.registerPatient(validRequest);
        });

        verify(patientRepository, never()).save(any(Patient.class));
    }
}
