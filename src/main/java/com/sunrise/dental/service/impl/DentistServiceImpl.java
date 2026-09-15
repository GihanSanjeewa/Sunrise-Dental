package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.DentistRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.dto.response.DentistResponse;
import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.entity.Dentist;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.repository.DentistRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.DentistService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DentistServiceImpl implements DentistService {

    private final DentistRepository dentistRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditService auditService;

    public DentistServiceImpl(DentistRepository dentistRepository,
                              AppointmentRepository appointmentRepository,
                              AuditService auditService) {
        this.dentistRepository = dentistRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
    }

    @Override
    public DentistResponse addDentist(DentistRequest request) {
        validateDentistRequest(request);

        Long maxId = dentistRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String dentistNumber = IdSequenceGenerator.generateDentistNumber(nextId);

        while (dentistRepository.existsByDentistNumber(dentistNumber)) {
            nextId++;
            dentistNumber = IdSequenceGenerator.generateDentistNumber(nextId);
        }

        Dentist dentist = new Dentist(
                dentistNumber,
                request.getName().trim(),
                request.getSpecialization().trim(),
                request.getContactNumber().trim(),
                request.getEmail().trim(),
                request.getAvailabilityStatus()
        );

        Dentist saved = dentistRepository.save(dentist);
        auditService.logAction("ADMIN", "ADD_DENTIST", "DENTIST", saved.getDentistNumber(),
                "Added dentist " + saved.getName());

        return mapToResponse(saved);
    }

    @Override
    public DentistResponse updateDentist(Long id, DentistRequest request) {
        validateDentistRequest(request);

        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + id));

        dentist.setName(request.getName().trim());
        dentist.setSpecialization(request.getSpecialization().trim());
        dentist.setContactNumber(request.getContactNumber().trim());
        dentist.setEmail(request.getEmail().trim());
        dentist.setAvailabilityStatus(request.getAvailabilityStatus());

        Dentist updated = dentistRepository.save(dentist);
        auditService.logAction("ADMIN", "UPDATE_DENTIST", "DENTIST", updated.getDentistNumber(),
                "Updated details for dentist " + updated.getName());

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public DentistResponse getDentistById(Long id) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + id));
        return mapToResponse(dentist);
    }

    @Override
    public DentistResponse updateAvailabilityStatus(Long id, DentistStatus status) {
        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + id));

        dentist.setAvailabilityStatus(status);
        Dentist updated = dentistRepository.save(dentist);
        auditService.logAction("ADMIN", "UPDATE_STATUS", "DENTIST", updated.getDentistNumber(),
                "Updated dentist status to " + status);

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DentistResponse> getAllDentists() {
        return dentistRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DentistResponse> getAvailableDentists() {
        return dentistRepository.findByAvailabilityStatus(DentistStatus.AVAILABLE).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DentistResponse> searchDentists(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllDentists();
        }
        return dentistRepository.searchDentists(query.trim()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDentistAppointments(Long dentistId) {
        if (!dentistRepository.existsById(dentistId)) {
            throw new ResourceNotFoundException("Dentist not found with ID: " + dentistId);
        }
        return appointmentRepository.findByDentistId(dentistId).stream()
                .map(this::mapToAppointmentResponse)
                .collect(Collectors.toList());
    }

    private void validateDentistRequest(DentistRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Dentist name cannot be empty.");
        }
        if (request.getSpecialization() == null || request.getSpecialization().trim().isEmpty()) {
            throw new ValidationException("Dentist specialization cannot be empty.");
        }
        if (request.getContactNumber() == null || request.getContactNumber().trim().isEmpty()) {
            throw new ValidationException("Contact phone number cannot be empty.");
        }
    }

    private DentistResponse mapToResponse(Dentist d) {
        return new DentistResponse(
                d.getId(),
                d.getDentistNumber(),
                d.getName(),
                d.getSpecialization(),
                d.getContactNumber(),
                d.getEmail(),
                d.getAvailabilityStatus(),
                d.getCreatedAt(),
                d.getUpdatedAt()
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
