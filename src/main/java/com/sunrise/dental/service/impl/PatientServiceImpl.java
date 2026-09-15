package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.PatientRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.dto.response.PatientResponse;
import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.exception.DuplicateResourceException;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.PatientService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditService auditService;

    public PatientServiceImpl(PatientRepository patientRepository,
                              AppointmentRepository appointmentRepository,
                              AuditService auditService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
    }

    @Override
    public PatientResponse registerPatient(PatientRequest request) {
        validatePatientRequest(request);

        if (patientRepository.existsByContactNumber(request.getContactNumber().trim())) {
            throw new DuplicateResourceException("A patient with contact number " + request.getContactNumber() + " already exists.");
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() &&
                patientRepository.existsByEmail(request.getEmail().trim())) {
            throw new DuplicateResourceException("A patient with email " + request.getEmail() + " already exists.");
        }

        Long maxId = patientRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String patientNumber = IdSequenceGenerator.generatePatientNumber(nextId);

        // Safety check if number exists
        while (patientRepository.existsByPatientNumber(patientNumber)) {
            nextId++;
            patientNumber = IdSequenceGenerator.generatePatientNumber(nextId);
        }

        Patient patient = new Patient(
                patientNumber,
                request.getFullName().trim(),
                request.getAddress().trim(),
                request.getContactNumber().trim(),
                request.getEmail() != null ? request.getEmail().trim() : null,
                request.getDateOfBirth(),
                request.getGender().trim().toUpperCase()
        );

        Patient saved = patientRepository.save(patient);
        auditService.logAction("RECEPTIONIST", "REGISTER", "PATIENT", saved.getPatientNumber(),
                "Registered new patient " + saved.getFullName());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return mapToResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getPatientByNumber(String patientNumber) {
        Patient patient = patientRepository.findByPatientNumber(patientNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No patient found with patient number: " + patientNumber));
        return mapToResponse(patient);
    }

    @Override
    public PatientResponse updatePatient(Long id, PatientRequest request) {
        validatePatientRequest(request);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        // Check if phone changed and belongs to someone else
        String newContact = request.getContactNumber().trim();
        if (!newContact.equals(patient.getContactNumber()) && patientRepository.existsByContactNumber(newContact)) {
            throw new DuplicateResourceException("Another patient with contact number " + newContact + " already exists.");
        }

        // Check if email changed and belongs to someone else
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim();
            if (!newEmail.equalsIgnoreCase(patient.getEmail()) && patientRepository.existsByEmail(newEmail)) {
                throw new DuplicateResourceException("Another patient with email " + newEmail + " already exists.");
            }
            patient.setEmail(newEmail);
        }

        patient.setFullName(request.getFullName().trim());
        patient.setAddress(request.getAddress().trim());
        patient.setContactNumber(newContact);
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender().trim().toUpperCase());

        Patient updated = patientRepository.save(patient);
        auditService.logAction("RECEPTIONIST", "UPDATE", "PATIENT", updated.getPatientNumber(),
                "Updated details for patient " + updated.getFullName());

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllPatients();
        }
        return patientRepository.searchPatients(query.trim()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getPatientAppointmentHistory(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with ID: " + patientId);
        }
        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        return appointments.stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    private void validatePatientRequest(PatientRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new ValidationException("Patient name cannot be empty.");
        }
        if (request.getContactNumber() == null || request.getContactNumber().trim().isEmpty()) {
            throw new ValidationException("Contact phone number cannot be empty.");
        }
        if (request.getDateOfBirth() == null) {
            throw new ValidationException("Date of birth cannot be empty.");
        }
    }

    private PatientResponse mapToResponse(Patient p) {
        return new PatientResponse(
                p.getId(),
                p.getPatientNumber(),
                p.getFullName(),
                p.getAddress(),
                p.getContactNumber(),
                p.getEmail(),
                p.getDateOfBirth(),
                p.getGender(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment a) {
        AppointmentResponse resp = new AppointmentResponse();
        resp.setId(a.getId());
        resp.setAppointmentNumber(a.getAppointmentNumber());
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
        return resp;
    }
}
