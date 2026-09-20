package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.MedicationRequest;
import com.sunrise.dental.dto.request.PrescriptionItemRequest;
import com.sunrise.dental.dto.request.PrescriptionRequest;
import com.sunrise.dental.dto.response.MedicationResponse;
import com.sunrise.dental.dto.response.PrescriptionItemResponse;
import com.sunrise.dental.dto.response.PrescriptionResponse;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.*;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.PrescriptionService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final MedicationRepository medicationRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final AuditService auditService;

    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   PrescriptionItemRepository prescriptionItemRepository,
                                   MedicationRepository medicationRepository,
                                   PatientRepository patientRepository,
                                   DentistRepository dentistRepository,
                                   ClinicalRecordRepository clinicalRecordRepository,
                                   AuditService auditService) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.medicationRepository = medicationRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.auditService = auditService;
    }

    @Override
    public PrescriptionResponse createPrescription(PrescriptionRequest request) {
        if (request.getPatientId() == null) {
            throw new ValidationException("Patient ID is required.");
        }
        if (request.getDentistId() == null) {
            throw new ValidationException("Dentist ID is required.");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + request.getDentistId()));

        ClinicalRecord cr = null;
        if (request.getClinicalRecordId() != null) {
            cr = clinicalRecordRepository.findById(request.getClinicalRecordId()).orElse(null);
        }

        Long maxId = prescriptionRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String rxNumber = IdSequenceGenerator.generatePrescriptionNumber(nextId);

        while (prescriptionRepository.existsByPrescriptionNumber(rxNumber)) {
            nextId++;
            rxNumber = IdSequenceGenerator.generatePrescriptionNumber(nextId);
        }

        LocalDate rxDate = request.getPrescriptionDate() != null ? request.getPrescriptionDate() : LocalDate.now();

        Prescription rx = new Prescription(
                rxNumber,
                patient,
                dentist,
                cr,
                rxDate,
                request.getNotes()
        );

        Prescription savedRx = prescriptionRepository.save(rx);
        List<PrescriptionItem> items = new ArrayList<>();

        if (request.getItems() != null) {
            for (PrescriptionItemRequest itemReq : request.getItems()) {
                Medication med = null;
                if (itemReq.getMedicationId() != null) {
                    med = medicationRepository.findById(itemReq.getMedicationId()).orElse(null);
                }

                PrescriptionItem item = new PrescriptionItem(
                        savedRx,
                        med,
                        itemReq.getMedicineName(),
                        itemReq.getDosage(),
                        itemReq.getFrequency(),
                        itemReq.getDuration(),
                        itemReq.getInstructions()
                );
                items.add(prescriptionItemRepository.save(item));
            }
        }

        savedRx.setItems(items);
        auditService.logAction("DENTIST", "CREATE", "PRESCRIPTION", savedRx.getPrescriptionNumber(),
                "Issued prescription for patient " + patient.getFullName() + " (" + patient.getPatientNumber() + ")");

        return mapToResponse(savedRx);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionById(Long id) {
        Prescription rx = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));
        return mapToResponse(rx);
    }

    @Override
    @Transactional(readOnly = true)
    public PrescriptionResponse getPrescriptionByNumber(String prescriptionNumber) {
        Prescription rx = prescriptionRepository.findByPrescriptionNumber(prescriptionNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with number: " + prescriptionNumber));
        return mapToResponse(rx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByPrescriptionDateDescCreatedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrescriptionResponse> getPrescriptionsByDentistId(Long dentistId) {
        return prescriptionRepository.findByDentistIdOrderByPrescriptionDateDesc(dentistId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> getAllMedications() {
        return medicationRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToMedicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicationResponse> searchMedications(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllMedications();
        }
        return medicationRepository.findByNameContainingIgnoreCaseOrGenericNameContainingIgnoreCase(query.trim(), query.trim())
                .stream()
                .map(this::mapToMedicationResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MedicationResponse createMedication(MedicationRequest request) {
        Medication med = new Medication(
                request.getName().trim(),
                request.getGenericName(),
                request.getDosageForm(),
                request.getDefaultDosage(),
                request.getDefaultFrequency(),
                request.getInstructions(),
                request.getIsActive() != null ? request.getIsActive() : true
        );
        Medication saved = medicationRepository.save(med);
        auditService.logAction("ADMIN", "CREATE", "MEDICATION", saved.getId().toString(),
                "Added formulary medication: " + saved.getName());
        return mapToMedicationResponse(saved);
    }

    @Override
    public void deletePrescription(Long id) {
        Prescription rx = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with ID: " + id));
        prescriptionRepository.delete(rx);
        auditService.logAction("ADMIN", "DELETE", "PRESCRIPTION", rx.getPrescriptionNumber(),
                "Deleted prescription " + rx.getPrescriptionNumber());
    }

    private PrescriptionResponse mapToResponse(Prescription rx) {
        PrescriptionResponse resp = new PrescriptionResponse();
        resp.setId(rx.getId());
        resp.setPrescriptionNumber(rx.getPrescriptionNumber());

        if (rx.getPatient() != null) {
            resp.setPatientId(rx.getPatient().getId());
            resp.setPatientNumber(rx.getPatient().getPatientNumber());
            resp.setPatientName(rx.getPatient().getFullName());
            resp.setPatientGender(rx.getPatient().getGender());
            if (rx.getPatient().getDateOfBirth() != null) {
                resp.setPatientAge(Period.between(rx.getPatient().getDateOfBirth(), LocalDate.now()).getYears());
            }
        }

        if (rx.getDentist() != null) {
            resp.setDentistId(rx.getDentist().getId());
            resp.setDentistName(rx.getDentist().getName());
            resp.setDentistSpecialization(rx.getDentist().getSpecialization());
        }

        if (rx.getClinicalRecord() != null) {
            resp.setClinicalRecordId(rx.getClinicalRecord().getId());
        }

        resp.setPrescriptionDate(rx.getPrescriptionDate());
        resp.setNotes(rx.getNotes());
        resp.setCreatedAt(rx.getCreatedAt());
        resp.setUpdatedAt(rx.getUpdatedAt());

        List<PrescriptionItem> items = prescriptionItemRepository.findByPrescriptionId(rx.getId());
        resp.setItems(items.stream().map(this::mapToItemResponse).collect(Collectors.toList()));

        return resp;
    }

    private PrescriptionItemResponse mapToItemResponse(PrescriptionItem it) {
        PrescriptionItemResponse resp = new PrescriptionItemResponse();
        resp.setId(it.getId());
        resp.setPrescriptionId(it.getPrescription().getId());
        if (it.getMedication() != null) {
            resp.setMedicationId(it.getMedication().getId());
        }
        resp.setMedicineName(it.getMedicineName());
        resp.setDosage(it.getDosage());
        resp.setFrequency(it.getFrequency());
        resp.setDuration(it.getDuration());
        resp.setInstructions(it.getInstructions());
        return resp;
    }

    private MedicationResponse mapToMedicationResponse(Medication m) {
        MedicationResponse resp = new MedicationResponse();
        resp.setId(m.getId());
        resp.setName(m.getName());
        resp.setGenericName(m.getGenericName());
        resp.setDosageForm(m.getDosageForm());
        resp.setDefaultDosage(m.getDefaultDosage());
        resp.setDefaultFrequency(m.getDefaultFrequency());
        resp.setInstructions(m.getInstructions());
        resp.setIsActive(m.getIsActive());
        resp.setCreatedAt(m.getCreatedAt());
        return resp;
    }
}
