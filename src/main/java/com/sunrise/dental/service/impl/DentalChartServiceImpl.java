package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.ToothHistoryRequest;
import com.sunrise.dental.dto.request.ToothRecordRequest;
import com.sunrise.dental.dto.response.DentalChartResponse;
import com.sunrise.dental.dto.response.ToothHistoryResponse;
import com.sunrise.dental.dto.response.ToothRecordResponse;
import com.sunrise.dental.entity.ClinicalRecord;
import com.sunrise.dental.entity.DentalToothRecord;
import com.sunrise.dental.entity.Patient;
import com.sunrise.dental.entity.ToothHistory;
import com.sunrise.dental.enums.ToothCondition;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.repository.ClinicalRecordRepository;
import com.sunrise.dental.repository.DentalToothRecordRepository;
import com.sunrise.dental.repository.PatientRepository;
import com.sunrise.dental.repository.ToothHistoryRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.DentalChartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DentalChartServiceImpl implements DentalChartService {

    // 32 adult permanent FDI teeth in 4 quadrants
    private static final int[] QUADRANT_1 = {18, 17, 16, 15, 14, 13, 12, 11};
    private static final int[] QUADRANT_2 = {21, 22, 23, 24, 25, 26, 27, 28};
    private static final int[] QUADRANT_3 = {38, 37, 36, 35, 34, 33, 32, 31};
    private static final int[] QUADRANT_4 = {41, 42, 43, 44, 45, 46, 47, 48};

    private final DentalToothRecordRepository dentalToothRecordRepository;
    private final ToothHistoryRepository toothHistoryRepository;
    private final PatientRepository patientRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final AuditService auditService;

    public DentalChartServiceImpl(DentalToothRecordRepository dentalToothRecordRepository,
                                  ToothHistoryRepository toothHistoryRepository,
                                  PatientRepository patientRepository,
                                  ClinicalRecordRepository clinicalRecordRepository,
                                  AuditService auditService) {
        this.dentalToothRecordRepository = dentalToothRecordRepository;
        this.toothHistoryRepository = toothHistoryRepository;
        this.patientRepository = patientRepository;
        this.clinicalRecordRepository = clinicalRecordRepository;
        this.auditService = auditService;
    }

    @Override
    public DentalChartResponse getDentalChartByPatientId(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        // Auto-initialize if patient has fewer than 32 tooth records
        List<DentalToothRecord> existing = dentalToothRecordRepository.findByPatientIdOrderByToothNumberAsc(patientId);
        if (existing.size() < 32) {
            initializePatientChart(patientId);
            existing = dentalToothRecordRepository.findByPatientIdOrderByToothNumberAsc(patientId);
        }

        Map<Integer, DentalToothRecord> toothMap = existing.stream()
                .collect(Collectors.toMap(DentalToothRecord::getToothNumber, t -> t, (a, b) -> a));

        DentalChartResponse response = new DentalChartResponse();
        response.setPatientId(patient.getId());
        response.setPatientNumber(patient.getPatientNumber());
        response.setPatientName(patient.getFullName());

        response.setUpperRight(buildToothList(QUADRANT_1, toothMap));
        response.setUpperLeft(buildToothList(QUADRANT_2, toothMap));
        response.setLowerLeft(buildToothList(QUADRANT_3, toothMap));
        response.setLowerRight(buildToothList(QUADRANT_4, toothMap));

        List<ToothRecordResponse> all = existing.stream()
                .map(this::mapToToothResponse)
                .collect(Collectors.toList());
        response.setAllTeeth(all);

        return response;
    }

    @Override
    public ToothRecordResponse updateToothCondition(ToothRecordRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        DentalToothRecord record = dentalToothRecordRepository
                .findByPatientIdAndToothNumber(patient.getId(), request.getToothNumber())
                .orElseGet(() -> new DentalToothRecord(
                        patient,
                        request.getToothNumber(),
                        request.getCondition(),
                        request.getStatus() != null ? request.getStatus() : "SOUND",
                        request.getNotes(),
                        request.getLastTreatmentDate()
                ));

        String oldCondition = record.getCondition().name();
        record.setCondition(request.getCondition());
        if (request.getStatus() != null) record.setStatus(request.getStatus());
        if (request.getNotes() != null) record.setNotes(request.getNotes());
        if (request.getLastTreatmentDate() != null) record.setLastTreatmentDate(request.getLastTreatmentDate());

        DentalToothRecord saved = dentalToothRecordRepository.save(record);

        if (!oldCondition.equals(saved.getCondition().name())) {
            ToothHistory history = new ToothHistory(
                    saved,
                    null,
                    "Condition update: " + oldCondition + " -> " + saved.getCondition().name(),
                    LocalDate.now(),
                    saved.getNotes()
            );
            toothHistoryRepository.save(history);
        }

        auditService.logAction("DENTIST", "UPDATE_TOOTH", "DENTAL_TOOTH",
                "Patient: " + patient.getPatientNumber() + ", Tooth: " + request.getToothNumber(),
                "Changed condition from " + oldCondition + " to " + saved.getCondition().name() + ". Notes: " + saved.getNotes());

        return mapToToothResponse(saved);
    }

    @Override
    public ToothHistoryResponse addToothHistory(ToothHistoryRequest request) {
        DentalToothRecord tooth = dentalToothRecordRepository.findById(request.getDentalToothId())
                .orElseThrow(() -> new ResourceNotFoundException("Tooth record not found with ID: " + request.getDentalToothId()));

        ClinicalRecord cr = null;
        if (request.getClinicalRecordId() != null) {
            cr = clinicalRecordRepository.findById(request.getClinicalRecordId()).orElse(null);
        }

        LocalDate procDate = request.getProcedureDate() != null ? request.getProcedureDate() : LocalDate.now();

        ToothHistory history = new ToothHistory(
                tooth,
                cr,
                request.getTreatmentName(),
                procDate,
                request.getNotes()
        );

        ToothHistory saved = toothHistoryRepository.save(history);

        // Also update last treatment date on the tooth record
        tooth.setLastTreatmentDate(procDate);
        dentalToothRecordRepository.save(tooth);

        return mapToHistoryResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ToothHistoryResponse> getToothHistory(Long dentalToothId) {
        return toothHistoryRepository.findByDentalToothIdOrderByProcedureDateDescCreatedAtDesc(dentalToothId)
                .stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void initializePatientChart(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        int[] all32 = {
                18, 17, 16, 15, 14, 13, 12, 11,
                21, 22, 23, 24, 25, 26, 27, 28,
                38, 37, 36, 35, 34, 33, 32, 31,
                41, 42, 43, 44, 45, 46, 47, 48
        };

        for (int toothNum : all32) {
            if (!dentalToothRecordRepository.existsByPatientIdAndToothNumber(patientId, toothNum)) {
                DentalToothRecord tooth = new DentalToothRecord(
                        patient,
                        toothNum,
                        ToothCondition.HEALTHY,
                        "SOUND",
                        null,
                        null
                );
                dentalToothRecordRepository.save(tooth);
            }
        }
    }

    private List<ToothRecordResponse> buildToothList(int[] quadrant, Map<Integer, DentalToothRecord> map) {
        List<ToothRecordResponse> list = new ArrayList<>();
        for (int num : quadrant) {
            DentalToothRecord record = map.get(num);
            if (record != null) {
                list.add(mapToToothResponse(record));
            }
        }
        return list;
    }

    private ToothRecordResponse mapToToothResponse(DentalToothRecord t) {
        ToothRecordResponse resp = new ToothRecordResponse();
        resp.setId(t.getId());
        resp.setPatientId(t.getPatient().getId());
        resp.setToothNumber(t.getToothNumber());
        resp.setCondition(t.getCondition());
        resp.setStatus(t.getStatus());
        resp.setNotes(t.getNotes());
        resp.setLastTreatmentDate(t.getLastTreatmentDate());

        List<ToothHistoryResponse> history = toothHistoryRepository
                .findByDentalToothIdOrderByProcedureDateDescCreatedAtDesc(t.getId())
                .stream()
                .map(this::mapToHistoryResponse)
                .collect(Collectors.toList());
        resp.setHistory(history);

        return resp;
    }

    private ToothHistoryResponse mapToHistoryResponse(ToothHistory h) {
        ToothHistoryResponse resp = new ToothHistoryResponse();
        resp.setId(h.getId());
        resp.setDentalToothId(h.getDentalTooth().getId());
        resp.setToothNumber(h.getDentalTooth().getToothNumber());
        if (h.getClinicalRecord() != null) {
            resp.setClinicalRecordId(h.getClinicalRecord().getId());
        }
        resp.setTreatmentName(h.getTreatmentName());
        resp.setProcedureDate(h.getProcedureDate());
        resp.setNotes(h.getNotes());
        resp.setCreatedAt(h.getCreatedAt());
        return resp;
    }
}
