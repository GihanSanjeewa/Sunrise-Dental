package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.AppointmentRequest;
import com.sunrise.dental.dto.request.AppointmentStatusUpdateRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.enums.AppointmentStatus;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.exception.AppointmentConflictException;
import com.sunrise.dental.exception.InvalidAppointmentException;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.*;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;
    private final BillRepository billRepository;
    private final AuditService auditService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  PatientRepository patientRepository,
                                  DentistRepository dentistRepository,
                                  TreatmentRepository treatmentRepository,
                                  BillRepository billRepository,
                                  AuditService auditService) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentRepository = treatmentRepository;
        this.billRepository = billRepository;
        this.auditService = auditService;
    }

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        validateAppointmentRequest(request);

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + request.getDentistId()));

        if (dentist.getAvailabilityStatus() != DentistStatus.AVAILABLE) {
            throw new ValidationException("Selected dentist is currently not available (Status: " + dentist.getAvailabilityStatus() + ").");
        }

        Treatment treatment = treatmentRepository.findById(request.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + request.getTreatmentId()));

        if (!"ACTIVE".equalsIgnoreCase(treatment.getStatus())) {
            throw new ValidationException("Selected treatment is currently not active in the catalog.");
        }

        // Atomic Double-Booking Conflict Prevention
        boolean isConflict = appointmentRepository.existsActiveSlotForDentist(
                dentist.getId(),
                request.getAppointmentDate(),
                request.getAppointmentTime()
        );

        if (isConflict) {
            throw new AppointmentConflictException("Selected dentist is already booked for this time.");
        }

        Long maxId = appointmentRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String aptNumber = IdSequenceGenerator.generateAppointmentNumber(nextId);

        while (appointmentRepository.existsByAppointmentNumber(aptNumber)) {
            nextId++;
            aptNumber = IdSequenceGenerator.generateAppointmentNumber(nextId);
        }

        long countToday = appointmentRepository.countByAppointmentDate(request.getAppointmentDate());
        String tokenNumber = IdSequenceGenerator.generateTokenNumber((int) countToday + 1);

        Appointment appointment = new Appointment(
                aptNumber,
                patient,
                dentist,
                treatment,
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                AppointmentStatus.BOOKED,
                request.getNotes() != null ? request.getNotes().trim() : null
        );
        appointment.setTokenNumber(tokenNumber);

        Appointment saved = appointmentRepository.save(appointment);
        auditService.logAction("RECEPTIONIST", "BOOK", "APPOINTMENT", saved.getAppointmentNumber(),
                "Booked appointment for " + patient.getFullName() + " with " + dentist.getName() + " on " + saved.getAppointmentDate() + " at " + saved.getAppointmentTime() + " (Token: " + tokenNumber + ")");

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        return mapToResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentByNumber(String appointmentNumber) {
        if (appointmentNumber == null || appointmentNumber.trim().isEmpty()) {
            throw new ValidationException("Appointment number cannot be empty.");
        }
        Appointment appointment = appointmentRepository.findByAppointmentNumber(appointmentNumber.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No appointment found with the provided appointment number."));
        return mapToResponse(appointment);
    }

    @Override
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        validateAppointmentRequest(request);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + request.getDentistId()));

        Treatment treatment = treatmentRepository.findById(request.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + request.getTreatmentId()));

        // Check if conflict exists excluding current appointment
        boolean isConflict = appointmentRepository.existsActiveSlotForDentistExcludingId(
                dentist.getId(),
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                id
        );

        if (isConflict) {
            throw new AppointmentConflictException("Selected dentist is already booked for this time.");
        }

        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setAppointmentTime(request.getAppointmentTime());
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes().trim());
        }

        Appointment updated = appointmentRepository.save(appointment);
        auditService.logAction("RECEPTIONIST", "UPDATE", "APPOINTMENT", updated.getAppointmentNumber(),
                "Updated appointment details");

        return mapToResponse(updated);
    }

    @Override
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusUpdateRequest request) {
        if (request.getStatus() == null) {
            throw new ValidationException("Appointment status cannot be empty.");
        }

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        appointment.setStatus(request.getStatus());
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            String existingNotes = appointment.getNotes() != null ? appointment.getNotes() + " | " : "";
            appointment.setNotes(existingNotes + request.getNotes().trim());
        }

        Appointment updated = appointmentRepository.save(appointment);
        auditService.logAction("STAFF", "UPDATE_STATUS", "APPOINTMENT", updated.getAppointmentNumber(),
                "Changed appointment status to " + request.getStatus());

        return mapToResponse(updated);
    }

    @Override
    public AppointmentResponse cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ValidationException("Appointment is already cancelled.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);

        auditService.logAction("RECEPTIONIST", "CANCEL", "APPOINTMENT", saved.getAppointmentNumber(),
                "Cancelled appointment. Time slot released.");

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> searchAppointments(String appointmentNumber, String patientNumber,
                                                        String patientName, String contactNumber,
                                                        Long dentistId, LocalDate date, AppointmentStatus status) {
        // If searching specifically by appointment number
        if (appointmentNumber != null && !appointmentNumber.trim().isEmpty()) {
            return appointmentRepository.findByAppointmentNumber(appointmentNumber.trim())
                    .map(a -> List.of(mapToResponse(a)))
                    .orElse(List.of());
        }

        String aptNumParam = (appointmentNumber != null && !appointmentNumber.trim().isEmpty()) ? appointmentNumber.trim() : null;
        String patNumParam = (patientNumber != null && !patientNumber.trim().isEmpty()) ? patientNumber.trim() : null;
        String patNameParam = (patientName != null && !patientName.trim().isEmpty()) ? patientName.trim() : null;
        String contactParam = (contactNumber != null && !contactNumber.trim().isEmpty()) ? contactNumber.trim() : null;

        return appointmentRepository.searchAppointments(aptNumParam, patNumParam, patNameParam, contactParam, dentistId, date, status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getTodayAppointments() {
        return appointmentRepository.findByAppointmentDate(LocalDate.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDailyQueue(LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        return appointmentRepository.findByAppointmentDate(queryDate).stream()
                .sorted((a1, a2) -> a1.getAppointmentTime().compareTo(a2.getAppointmentTime()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponse callPatient(Long id) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        apt.setStatus(AppointmentStatus.CALLED);
        Appointment saved = appointmentRepository.save(apt);
        auditService.logAction("STAFF", "CALL_PATIENT", "APPOINTMENT", saved.getAppointmentNumber(),
                "Called patient " + saved.getPatient().getFullName() + " (Token: " + saved.getTokenNumber() + ")");
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse startTreatment(Long id) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        apt.setStatus(AppointmentStatus.IN_TREATMENT);
        Appointment saved = appointmentRepository.save(apt);
        auditService.logAction("DENTIST", "START_TREATMENT", "APPOINTMENT", saved.getAppointmentNumber(),
                "Started treatment for " + saved.getPatient().getFullName());
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse completeVisit(Long id) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        apt.setStatus(AppointmentStatus.COMPLETED);
        Appointment saved = appointmentRepository.save(apt);
        auditService.logAction("STAFF", "COMPLETE_VISIT", "APPOINTMENT", saved.getAppointmentNumber(),
                "Completed visit for " + saved.getPatient().getFullName());
        return mapToResponse(saved);
    }

    @Override
    public AppointmentResponse markNoShow(Long id) {
        Appointment apt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
        apt.setStatus(AppointmentStatus.NO_SHOW);
        Appointment saved = appointmentRepository.save(apt);
        auditService.logAction("STAFF", "MARK_NO_SHOW", "APPOINTMENT", saved.getAppointmentNumber(),
                "Marked patient as No-Show for appointment " + saved.getAppointmentNumber());
        return mapToResponse(saved);
    }

    private void validateAppointmentRequest(AppointmentRequest request) {
        if (request.getPatientId() == null) {
            throw new ValidationException("Patient ID cannot be empty.");
        }
        if (request.getDentistId() == null) {
            throw new ValidationException("Dentist ID cannot be empty.");
        }
        if (request.getTreatmentId() == null) {
            throw new ValidationException("Treatment ID cannot be empty.");
        }
        if (request.getAppointmentDate() == null) {
            throw new ValidationException("Appointment date cannot be empty.");
        }
        if (request.getAppointmentTime() == null) {
            throw new ValidationException("Appointment time cannot be empty.");
        }

        LocalDate today = LocalDate.now();
        if (request.getAppointmentDate().isBefore(today)) {
            throw new InvalidAppointmentException("Appointment cannot be created in an invalid past date.");
        }
        if (request.getAppointmentDate().isEqual(today) && request.getAppointmentTime().isBefore(LocalTime.now())) {
            throw new InvalidAppointmentException("Appointment cannot be created in an invalid past time today.");
        }
    }

    private AppointmentResponse mapToResponse(Appointment a) {
        AppointmentResponse resp = new AppointmentResponse();
        resp.setId(a.getId());
        resp.setAppointmentNumber(a.getAppointmentNumber());
        resp.setTokenNumber(a.getTokenNumber());

        if (a.getPatient() != null) {
            resp.setPatientId(a.getPatient().getId());
            resp.setPatientNumber(a.getPatient().getPatientNumber());
            resp.setPatientName(a.getPatient().getFullName());
            resp.setPatientContactNumber(a.getPatient().getContactNumber());
            resp.setPatientAddress(a.getPatient().getAddress());
            resp.setPatientEmail(a.getPatient().getEmail());
        }

        if (a.getDentist() != null) {
            resp.setDentistId(a.getDentist().getId());
            resp.setDentistNumber(a.getDentist().getDentistNumber());
            resp.setDentistName(a.getDentist().getName());
            resp.setDentistSpecialization(a.getDentist().getSpecialization());
        }

        if (a.getTreatment() != null) {
            resp.setTreatmentId(a.getTreatment().getId());
            resp.setTreatmentCode(a.getTreatment().getTreatmentCode());
            resp.setTreatmentName(a.getTreatment().getTreatmentName());
            resp.setTreatmentCost(a.getTreatment().getTreatmentCost());
            resp.setConsultationFee(a.getTreatment().getConsultationFee());
        }

        resp.setAppointmentDate(a.getAppointmentDate());
        resp.setAppointmentTime(a.getAppointmentTime());
        resp.setStatus(a.getStatus());
        resp.setNotes(a.getNotes());
        resp.setCreatedAt(a.getCreatedAt());
        resp.setUpdatedAt(a.getUpdatedAt());

        // Check if an existing bill is attached
        Optional<Bill> billOpt = billRepository.findByAppointmentId(a.getId());
        if (billOpt.isPresent()) {
            resp.setHasBill(true);
            resp.setBillId(billOpt.get().getId());
            resp.setBillNumber(billOpt.get().getBillNumber());
        } else {
            resp.setHasBill(false);
        }

        return resp;
    }
}
