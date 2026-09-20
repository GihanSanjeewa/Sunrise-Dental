package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.TreatmentSessionRequest;
import com.sunrise.dental.dto.response.TreatmentSessionResponse;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.enums.SessionStatus;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.*;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.TreatmentSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TreatmentSessionServiceImpl implements TreatmentSessionService {

    private final TreatmentSessionRepository treatmentSessionRepository;
    private final TreatmentPlanRepository treatmentPlanRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditService auditService;

    public TreatmentSessionServiceImpl(TreatmentSessionRepository treatmentSessionRepository,
                                       TreatmentPlanRepository treatmentPlanRepository,
                                       PatientRepository patientRepository,
                                       DentistRepository dentistRepository,
                                       AppointmentRepository appointmentRepository,
                                       AuditService auditService) {
        this.treatmentSessionRepository = treatmentSessionRepository;
        this.treatmentPlanRepository = treatmentPlanRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
    }

    @Override
    public TreatmentSessionResponse createSession(TreatmentSessionRequest request) {
        if (request.getTreatmentPlanId() == null) {
            throw new ValidationException("Treatment plan ID is required.");
        }
        if (request.getPatientId() == null) {
            throw new ValidationException("Patient ID is required.");
        }
        if (request.getDentistId() == null) {
            throw new ValidationException("Dentist ID is required.");
        }

        TreatmentPlan plan = treatmentPlanRepository.findById(request.getTreatmentPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan not found with ID: " + request.getTreatmentPlanId()));

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + request.getDentistId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId()).orElse(null);
        }

        int sessionNum = request.getSessionNumber() != null ? request.getSessionNumber() :
                treatmentSessionRepository.findByTreatmentPlanIdOrderBySessionNumberAsc(plan.getId()).size() + 1;

        LocalDate sessionDate = request.getSessionDate() != null ? request.getSessionDate() : LocalDate.now();

        TreatmentSession session = new TreatmentSession(
                plan,
                patient,
                dentist,
                appointment,
                sessionNum,
                sessionDate,
                request.getCompletedTreatment(),
                request.getRelatedTeeth(),
                request.getStatus() != null ? request.getStatus() : SessionStatus.SCHEDULED,
                request.getNotes()
        );

        TreatmentSession saved = treatmentSessionRepository.save(session);
        auditService.logAction("DENTIST", "CREATE", "TREATMENT_SESSION", saved.getId().toString(),
                "Created session #" + sessionNum + " for treatment plan " + plan.getPlanNumber());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentSessionResponse getSessionById(Long id) {
        TreatmentSession session = treatmentSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment session not found with ID: " + id));
        return mapToResponse(session);
    }

    @Override
    public TreatmentSessionResponse updateSession(Long id, TreatmentSessionRequest request) {
        TreatmentSession session = treatmentSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment session not found with ID: " + id));

        if (request.getCompletedTreatment() != null) session.setCompletedTreatment(request.getCompletedTreatment());
        if (request.getRelatedTeeth() != null) session.setRelatedTeeth(request.getRelatedTeeth());
        if (request.getStatus() != null) session.setStatus(request.getStatus());
        if (request.getNotes() != null) session.setNotes(request.getNotes());
        if (request.getSessionDate() != null) session.setSessionDate(request.getSessionDate());

        TreatmentSession updated = treatmentSessionRepository.save(session);
        auditService.logAction("DENTIST", "UPDATE", "TREATMENT_SESSION", updated.getId().toString(),
                "Updated session #" + updated.getSessionNumber());

        return mapToResponse(updated);
    }

    @Override
    public TreatmentSessionResponse updateSessionStatus(Long id, SessionStatus status, String notes) {
        TreatmentSession session = treatmentSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment session not found with ID: " + id));

        session.setStatus(status);
        if (notes != null && !notes.trim().isEmpty()) {
            String current = session.getNotes() != null ? session.getNotes() + " | " : "";
            session.setNotes(current + notes.trim());
        }

        TreatmentSession updated = treatmentSessionRepository.save(session);
        auditService.logAction("DENTIST", "UPDATE_STATUS", "TREATMENT_SESSION", updated.getId().toString(),
                "Changed session #" + updated.getSessionNumber() + " status to " + status);

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentSessionResponse> getSessionsByTreatmentPlanId(Long treatmentPlanId) {
        return treatmentSessionRepository.findByTreatmentPlanIdOrderBySessionNumberAsc(treatmentPlanId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentSessionResponse> getSessionsByPatientId(Long patientId) {
        return treatmentSessionRepository.findByPatientIdOrderBySessionDateDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentSessionResponse> getSessionsByDentistAndDate(Long dentistId, LocalDate date) {
        return treatmentSessionRepository.findByDentistIdAndSessionDate(dentistId, date != null ? date : LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSession(Long id) {
        TreatmentSession session = treatmentSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment session not found with ID: " + id));
        treatmentSessionRepository.delete(session);
        auditService.logAction("ADMIN", "DELETE", "TREATMENT_SESSION", id.toString(),
                "Deleted treatment session #" + session.getSessionNumber());
    }

    private TreatmentSessionResponse mapToResponse(TreatmentSession s) {
        TreatmentSessionResponse resp = new TreatmentSessionResponse();
        resp.setId(s.getId());
        if (s.getTreatmentPlan() != null) {
            resp.setTreatmentPlanId(s.getTreatmentPlan().getId());
            resp.setTreatmentPlanNumber(s.getTreatmentPlan().getPlanNumber());
            resp.setTreatmentPlanTitle(s.getTreatmentPlan().getTitle());
        }
        if (s.getPatient() != null) {
            resp.setPatientId(s.getPatient().getId());
            resp.setPatientNumber(s.getPatient().getPatientNumber());
            resp.setPatientName(s.getPatient().getFullName());
        }
        if (s.getDentist() != null) {
            resp.setDentistId(s.getDentist().getId());
            resp.setDentistName(s.getDentist().getName());
        }
        if (s.getAppointment() != null) {
            resp.setAppointmentId(s.getAppointment().getId());
            resp.setAppointmentNumber(s.getAppointment().getAppointmentNumber());
        }
        resp.setSessionNumber(s.getSessionNumber());
        resp.setSessionDate(s.getSessionDate());
        resp.setCompletedTreatment(s.getCompletedTreatment());
        resp.setRelatedTeeth(s.getRelatedTeeth());
        resp.setStatus(s.getStatus());
        resp.setNotes(s.getNotes());
        resp.setCreatedAt(s.getCreatedAt());
        resp.setUpdatedAt(s.getUpdatedAt());
        return resp;
    }
}
