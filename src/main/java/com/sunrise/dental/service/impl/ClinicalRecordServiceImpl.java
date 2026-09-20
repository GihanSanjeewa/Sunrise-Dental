package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.ClinicalRecordRequest;
import com.sunrise.dental.dto.response.ClinicalRecordResponse;
import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.entity.ClinicalRecord;
import com.sunrise.dental.entity.Dentist;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.repository.ClinicalRecordRepository;
import com.sunrise.dental.repository.DentistRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.ClinicalRecordService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClinicalRecordServiceImpl implements ClinicalRecordService {

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditService auditService;

    public ClinicalRecordServiceImpl(ClinicalRecordRepository clinicalRecordRepository,
                                     PatientRepository patientRepository,
                                     DentistRepository dentistRepository,
                                     AppointmentRepository appointmentRepository,
                                     AuditService auditService) {
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
    }

    @Override
    public ClinicalRecordResponse createClinicalRecord(ClinicalRecordRequest request) {
        if (request.getPatientId() == null) {
            throw new ValidationException("Patient ID cannot be null.");
        }
        if (request.getDentistId() == null) {
            throw new ValidationException("Dentist ID cannot be null.");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + request.getDentistId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId()).orElse(null);
        }

        Long maxId = clinicalRecordRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String recordNumber = IdSequenceGenerator.generateClinicalRecordNumber(nextId);

        while (clinicalRecordRepository.existsByRecordNumber(recordNumber)) {
            nextId++;
            recordNumber = IdSequenceGenerator.generateClinicalRecordNumber(nextId);
        }

        LocalDate visitDate = request.getVisitDate() != null ? request.getVisitDate() : LocalDate.now();

        ClinicalRecord record = new ClinicalRecord(
                recordNumber,
                patient,
                dentist,
                appointment,
                visitDate,
                request.getChiefComplaint(),
                request.getMedicalHistory(),
                request.getAllergies(),
                request.getCurrentMedications(),
                request.getDiagnosis(),
                request.getClinicalNotes(),
                request.getTreatmentNotes(),
                request.getFollowUpNotes()
        );

        ClinicalRecord saved = clinicalRecordRepository.save(record);
        auditService.logAction("DENTIST", "CREATE", "CLINICAL_RECORD", saved.getRecordNumber(),
                "Created clinical encounter record for patient " + patient.getFullName() + " (" + patient.getPatientNumber() + ")");

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicalRecordResponse getClinicalRecordById(Long id) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found with ID: " + id));
        return mapToResponse(record);
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicalRecordResponse getClinicalRecordByNumber(String recordNumber) {
        ClinicalRecord record = clinicalRecordRepository.findByRecordNumber(recordNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found with number: " + recordNumber));
        return mapToResponse(record);
    }

    @Override
    public ClinicalRecordResponse updateClinicalRecord(Long id, ClinicalRecordRequest request) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found with ID: " + id));

        if (request.getChiefComplaint() != null) record.setChiefComplaint(request.getChiefComplaint());
        if (request.getMedicalHistory() != null) record.setMedicalHistory(request.getMedicalHistory());
        if (request.getAllergies() != null) record.setAllergies(request.getAllergies());
        if (request.getCurrentMedications() != null) record.setCurrentMedications(request.getCurrentMedications());
        if (request.getDiagnosis() != null) record.setDiagnosis(request.getDiagnosis());
        if (request.getClinicalNotes() != null) record.setClinicalNotes(request.getClinicalNotes());
        if (request.getTreatmentNotes() != null) record.setTreatmentNotes(request.getTreatmentNotes());
        if (request.getFollowUpNotes() != null) record.setFollowUpNotes(request.getFollowUpNotes());
        if (request.getVisitDate() != null) record.setVisitDate(request.getVisitDate());

        ClinicalRecord updated = clinicalRecordRepository.save(record);
        auditService.logAction("DENTIST", "UPDATE", "CLINICAL_RECORD", updated.getRecordNumber(),
                "Updated clinical record " + updated.getRecordNumber());

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicalRecordResponse> getClinicalRecordsByPatientId(Long patientId) {
        return clinicalRecordRepository.findByPatientIdOrderByVisitDateDescCreatedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicalRecordResponse> getClinicalRecordsByDentistId(Long dentistId) {
        return clinicalRecordRepository.findByDentistIdOrderByVisitDateDesc(dentistId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicalRecordResponse> getClinicalRecordsByDate(LocalDate date) {
        return clinicalRecordRepository.findByVisitDate(date != null ? date : LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteClinicalRecord(Long id) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found with ID: " + id));
        clinicalRecordRepository.delete(record);
        auditService.logAction("ADMIN", "DELETE", "CLINICAL_RECORD", record.getRecordNumber(),
                "Deleted clinical record " + record.getRecordNumber());
    }

    private ClinicalRecordResponse mapToResponse(ClinicalRecord r) {
        ClinicalRecordResponse resp = new ClinicalRecordResponse();
        resp.setId(r.getId());
        resp.setRecordNumber(r.getRecordNumber());

        if (r.getPatient() != null) {
            resp.setPatientId(r.getPatient().getId());
            resp.setPatientNumber(r.getPatient().getPatientNumber());
            resp.setPatientName(r.getPatient().getFullName());
        }

        if (r.getDentist() != null) {
            resp.setDentistId(r.getDentist().getId());
            resp.setDentistName(r.getDentist().getName());
        }

        if (r.getAppointment() != null) {
            resp.setAppointmentId(r.getAppointment().getId());
            resp.setAppointmentNumber(r.getAppointment().getAppointmentNumber());
        }

        resp.setVisitDate(r.getVisitDate());
        resp.setChiefComplaint(r.getChiefComplaint());
        resp.setMedicalHistory(r.getMedicalHistory());
        resp.setAllergies(r.getAllergies());
        resp.setCurrentMedications(r.getCurrentMedications());
        resp.setDiagnosis(r.getDiagnosis());
        resp.setClinicalNotes(r.getClinicalNotes());
        resp.setTreatmentNotes(r.getTreatmentNotes());
        resp.setFollowUpNotes(r.getFollowUpNotes());
        resp.setCreatedAt(r.getCreatedAt());
        resp.setUpdatedAt(r.getUpdatedAt());

        return resp;
    }
}
