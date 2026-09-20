package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.DiagnosisRequest;
import com.sunrise.dental.dto.response.DiagnosisResponse;
import com.sunrise.dental.entity.ClinicalRecord;
import com.sunrise.dental.entity.Dentist;
import com.sunrise.dental.entity.Diagnosis;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.ClinicalRecordRepository;
import com.sunrise.dental.repository.DentistRepository;
import com.sunrise.dental.repository.DiagnosisRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.DiagnosisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final AuditService auditService;

    public DiagnosisServiceImpl(DiagnosisRepository diagnosisRepository,
                                PatientRepository patientRepository,
                                DentistRepository dentistRepository,
                                ClinicalRecordRepository clinicalRecordRepository,
                                AuditService auditService) {
        this.diagnosisRepository = diagnosisRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.auditService = auditService;
    }

    @Override
    public DiagnosisResponse createDiagnosis(DiagnosisRequest request) {
        if (request.getPatientId() == null) {
            throw new ValidationException("Patient ID cannot be null.");
        }
        if (request.getDentistId() == null) {
            throw new ValidationException("Dentist ID cannot be null.");
        }
        if (request.getDiagnosisName() == null || request.getDiagnosisName().trim().isEmpty()) {
            throw new ValidationException("Diagnosis name cannot be empty.");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + request.getDentistId()));

        ClinicalRecord cr = null;
        if (request.getClinicalRecordId() != null) {
            cr = clinicalRecordRepository.findById(request.getClinicalRecordId()).orElse(null);
        }

        LocalDate diagDate = request.getDiagnosedDate() != null ? request.getDiagnosedDate() : LocalDate.now();

        Diagnosis diagnosis = new Diagnosis(
                cr,
                patient,
                dentist,
                request.getDiagnosisCode(),
                request.getDiagnosisName().trim(),
                request.getNotes(),
                diagDate,
                request.getRelatedTeeth(),
                request.getStatus() != null ? request.getStatus() : "ACTIVE"
        );

        Diagnosis saved = diagnosisRepository.save(diagnosis);
        auditService.logAction("DENTIST", "CREATE", "DIAGNOSIS", saved.getId().toString(),
                "Recorded diagnosis '" + saved.getDiagnosisName() + "' for patient " + patient.getFullName());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DiagnosisResponse getDiagnosisById(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with ID: " + id));
        return mapToResponse(diagnosis);
    }

    @Override
    public DiagnosisResponse updateDiagnosis(Long id, DiagnosisRequest request) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with ID: " + id));

        if (request.getDiagnosisCode() != null) diagnosis.setDiagnosisCode(request.getDiagnosisCode());
        if (request.getDiagnosisName() != null) diagnosis.setDiagnosisName(request.getDiagnosisName().trim());
        if (request.getNotes() != null) diagnosis.setNotes(request.getNotes());
        if (request.getRelatedTeeth() != null) diagnosis.setRelatedTeeth(request.getRelatedTeeth());
        if (request.getStatus() != null) diagnosis.setStatus(request.getStatus());
        if (request.getDiagnosedDate() != null) diagnosis.setDiagnosedDate(request.getDiagnosedDate());

        Diagnosis updated = diagnosisRepository.save(diagnosis);
        auditService.logAction("DENTIST", "UPDATE", "DIAGNOSIS", updated.getId().toString(),
                "Updated diagnosis '" + updated.getDiagnosisName() + "'");

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisResponse> getDiagnosesByPatientId(Long patientId) {
        return diagnosisRepository.findByPatientIdOrderByDiagnosedDateDescCreatedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisResponse> getDiagnosesByClinicalRecordId(Long clinicalRecordId) {
        return diagnosisRepository.findByClinicalRecordId(clinicalRecordId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDiagnosis(Long id) {
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with ID: " + id));
        diagnosisRepository.delete(diagnosis);
        auditService.logAction("ADMIN", "DELETE", "DIAGNOSIS", id.toString(),
                "Deleted diagnosis '" + diagnosis.getDiagnosisName() + "'");
    }

    private DiagnosisResponse mapToResponse(Diagnosis d) {
        DiagnosisResponse resp = new DiagnosisResponse();
        resp.setId(d.getId());
        if (d.getClinicalRecord() != null) {
            resp.setClinicalRecordId(d.getClinicalRecord().getId());
        }
        if (d.getPatient() != null) {
            resp.setPatientId(d.getPatient().getId());
            resp.setPatientNumber(d.getPatient().getPatientNumber());
            resp.setPatientName(d.getPatient().getFullName());
        }
        if (d.getDentist() != null) {
            resp.setDentistId(d.getDentist().getId());
            resp.setDentistName(d.getDentist().getName());
        }
        resp.setDiagnosisCode(d.getDiagnosisCode());
        resp.setDiagnosisName(d.getDiagnosisName());
        resp.setNotes(d.getNotes());
        resp.setDiagnosedDate(d.getDiagnosedDate());
        resp.setRelatedTeeth(d.getRelatedTeeth());
        resp.setStatus(d.getStatus());
        resp.setCreatedAt(d.getCreatedAt());
        resp.setUpdatedAt(d.getUpdatedAt());
        return resp;
    }
}
