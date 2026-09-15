package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.AppointmentRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.entity.Dentist;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.entity.Treatment;
import com.sunrise.dental.enums.AppointmentStatus;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.exception.AppointmentConflictException;
import com.sunrise.dental.exception.InvalidAppointmentException;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.repository.*;
import com.sunrise.dental.service.impl.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private BillRepository billRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private Patient mockPatient;
    private Dentist mockDentist;
    private Treatment mockTreatment;
    private AppointmentRequest validRequest;

    @BeforeEach
    void setUp() {
        mockPatient = new Patient("P-000001", "Sunil Wickramasinghe", "Colombo 03", "0771234567", "sunil@gmail.com", LocalDate.of(1990, 1, 1), "MALE");
        mockPatient.setId(1L);

        mockDentist = new Dentist("D-000001", "Dr. Rohan Perera", "Orthodontics", "0772221100", "rohan@sunrisedental.lk", DentistStatus.AVAILABLE);
        mockDentist.setId(1L);

        mockTreatment = new Treatment("TRT-001", "Dental Consultation", "Consultation", BigDecimal.valueOf(0.00), BigDecimal.valueOf(2000.00), "ACTIVE");
        mockTreatment.setId(1L);

        validRequest = new AppointmentRequest(
                1L,
                1L,
                1L,
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                "Initial consultation"
        );
    }

    @Test
    @DisplayName("TC005: Successfully create appointment when slot is available")
    void shouldCreateAppointmentSuccessfully() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(mockDentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(mockTreatment));
        when(appointmentRepository.existsActiveSlotForDentist(eq(1L), any(LocalDate.class), any(LocalTime.class))).thenReturn(false);
        when(appointmentRepository.findMaxId()).thenReturn(5L);
        when(appointmentRepository.existsByAppointmentNumber(anyString())).thenReturn(false);

        Appointment savedAppointment = new Appointment("APT-2026-000006", mockPatient, mockDentist, mockTreatment,
                validRequest.getAppointmentDate(), validRequest.getAppointmentTime(), AppointmentStatus.BOOKED, "Initial consultation");
        savedAppointment.setId(6L);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(savedAppointment);
        when(billRepository.findByAppointmentId(anyLong())).thenReturn(Optional.empty());

        AppointmentResponse response = appointmentService.createAppointment(validRequest);

        assertNotNull(response);
        assertEquals("APT-2026-000006", response.getAppointmentNumber());
        assertEquals("Sunil Wickramasinghe", response.getPatientName());
        assertEquals("Dr. Rohan Perera", response.getDentistName());
        assertEquals(AppointmentStatus.BOOKED, response.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("TC006 / TDD Demonstration: Prevent double booking when dentist is already booked")
    void shouldRejectAppointmentWhenDentistAlreadyBooked() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(mockPatient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(mockDentist));
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(mockTreatment));

        // Simulate conflict in repository
        when(appointmentRepository.existsActiveSlotForDentist(eq(1L), eq(validRequest.getAppointmentDate()), eq(validRequest.getAppointmentTime())))
                .thenReturn(true);

        AppointmentConflictException exception = assertThrows(AppointmentConflictException.class, () -> {
            appointmentService.createAppointment(validRequest);
        });

        assertEquals("Selected dentist is already booked for this time.", exception.getMessage());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("TC012: Reject appointment when appointment date is in past")
    void shouldRejectAppointmentWhenInPastDate() {
        AppointmentRequest pastRequest = new AppointmentRequest(
                1L, 1L, 1L,
                LocalDate.now().minusDays(1),
                LocalTime.of(10, 0),
                "Past booking"
        );

        assertThrows(InvalidAppointmentException.class, () -> {
            appointmentService.createAppointment(pastRequest);
        });

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("TC007: Search valid appointment by appointment number")
    void shouldSearchAppointmentByNumber() {
        Appointment appointment = new Appointment("APT-2026-000001", mockPatient, mockDentist, mockTreatment,
                LocalDate.now().plusDays(2), LocalTime.of(11, 0), AppointmentStatus.CONFIRMED, "Scaling");
        appointment.setId(1L);

        when(appointmentRepository.findByAppointmentNumber("APT-2026-000001")).thenReturn(Optional.of(appointment));
        when(billRepository.findByAppointmentId(1L)).thenReturn(Optional.empty());

        AppointmentResponse response = appointmentService.getAppointmentByNumber("APT-2026-000001");

        assertNotNull(response);
        assertEquals("APT-2026-000001", response.getAppointmentNumber());
        assertEquals("Sunil Wickramasinghe", response.getPatientName());
        assertEquals(AppointmentStatus.CONFIRMED, response.getStatus());
    }

    @Test
    @DisplayName("TC008: Search non-existent appointment number returns error")
    void shouldThrowExceptionWhenAppointmentNumberNotFound() {
        when(appointmentRepository.findByAppointmentNumber("APT-999999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            appointmentService.getAppointmentByNumber("APT-999999");
        });

        assertEquals("No appointment found with the provided appointment number.", exception.getMessage());
    }

    @Test
    @DisplayName("TC013: Cancel appointment successfully releases time slot")
    void shouldCancelAppointmentSuccessfully() {
        Appointment appointment = new Appointment("APT-2026-000001", mockPatient, mockDentist, mockTreatment,
                LocalDate.now().plusDays(2), LocalTime.of(11, 0), AppointmentStatus.BOOKED, "Scaling");
        appointment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(billRepository.findByAppointmentId(1L)).thenReturn(Optional.empty());

        AppointmentResponse response = appointmentService.cancelAppointment(1L);

        assertEquals(AppointmentStatus.CANCELLED, response.getStatus());
        verify(auditService, times(1)).logAction(eq("RECEPTIONIST"), eq("CANCEL"), eq("APPOINTMENT"), anyString(), anyString());
    }
}
