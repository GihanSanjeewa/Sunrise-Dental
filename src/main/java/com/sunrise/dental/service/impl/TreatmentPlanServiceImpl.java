package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.TreatmentPlanItemRequest;
import com.sunrise.dental.dto.request.TreatmentPlanRequest;
import com.sunrise.dental.dto.response.TreatmentPlanItemResponse;
import com.sunrise.dental.dto.response.TreatmentPlanResponse;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.enums.TreatmentItemStatus;
import com.sunrise.dental.enums.TreatmentPlanStatus;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.*;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.TreatmentPlanService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TreatmentPlanServiceImpl implements TreatmentPlanService {

    private final TreatmentPlanRepository treatmentPlanRepository;
    private final TreatmentPlanItemRepository treatmentPlanItemRepository;
    private final PatientRepository patientRepository;
    private final DentistRepository dentistRepository;
    private final TreatmentRepository treatmentRepository;
    private final AuditService auditService;

    public TreatmentPlanServiceImpl(TreatmentPlanRepository treatmentPlanRepository,
                                    TreatmentPlanItemRepository treatmentPlanItemRepository,
                                    PatientRepository patientRepository,
                                    DentistRepository dentistRepository,
                                    TreatmentRepository treatmentRepository,
                                    AuditService auditService) {
        this.treatmentPlanRepository = treatmentPlanRepository;
        this.treatmentPlanItemRepository = treatmentPlanItemRepository;
        this.patientRepository = patientRepository;
        this.dentistRepository = dentistRepository;
        this.treatmentRepository = treatmentRepository;
        this.auditService = auditService;
    }

    @Override
    public TreatmentPlanResponse createTreatmentPlan(TreatmentPlanRequest request) {
        if (request.getPatientId() == null) {
            throw new ValidationException("Patient ID is required.");
        }
        if (request.getDentistId() == null) {
            throw new ValidationException("Dentist ID is required.");
        }
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new ValidationException("Treatment plan title cannot be empty.");
        }

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Dentist dentist = dentistRepository.findById(request.getDentistId())
                .orElseThrow(() -> new ResourceNotFoundException("Dentist not found with ID: " + request.getDentistId()));

        Long maxId = treatmentPlanRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String planNumber = IdSequenceGenerator.generateTreatmentPlanNumber(nextId);

        while (treatmentPlanRepository.existsByPlanNumber(planNumber)) {
            nextId++;
            planNumber = IdSequenceGenerator.generateTreatmentPlanNumber(nextId);
        }

        TreatmentPlan plan = new TreatmentPlan(
                planNumber,
                patient,
                dentist,
                request.getTitle().trim(),
                request.getStatus() != null ? request.getStatus() : TreatmentPlanStatus.PLANNED,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                request.getNotes()
        );

        TreatmentPlan savedPlan = treatmentPlanRepository.save(plan);

        BigDecimal totalEstimated = BigDecimal.ZERO;
        List<TreatmentPlanItem> items = new ArrayList<>();

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (TreatmentPlanItemRequest itemReq : request.getItems()) {
                Treatment treatment = treatmentRepository.findById(itemReq.getTreatmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + itemReq.getTreatmentId()));

                int qty = itemReq.getQuantity() != null && itemReq.getQuantity() > 0 ? itemReq.getQuantity() : 1;
                // Zero-hardcoding: pull pricing from Treatment catalog if not explicitly overridden
                BigDecimal unitCost = itemReq.getUnitCost() != null ? itemReq.getUnitCost() : treatment.getTreatmentCost();
                BigDecimal estCost = unitCost.multiply(BigDecimal.valueOf(qty));

                TreatmentPlanItem item = new TreatmentPlanItem(
                        savedPlan,
                        treatment,
                        itemReq.getToothNumber(),
                        qty,
                        unitCost,
                        estCost,
                        BigDecimal.ZERO,
                        itemReq.getStatus() != null ? itemReq.getStatus() : TreatmentItemStatus.PENDING,
                        itemReq.getNotes()
                );

                TreatmentPlanItem savedItem = treatmentPlanItemRepository.save(item);
                items.add(savedItem);
                totalEstimated = totalEstimated.add(estCost);
            }
        }

        savedPlan.setEstimatedCost(totalEstimated);
        savedPlan.setItems(items);
        treatmentPlanRepository.save(savedPlan);

        auditService.logAction("DENTIST", "CREATE", "TREATMENT_PLAN", savedPlan.getPlanNumber(),
                "Created treatment plan '" + savedPlan.getTitle() + "' for patient " + patient.getFullName() + ". Total Est: Rs. " + totalEstimated);

        return mapToResponse(savedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentPlanResponse getTreatmentPlanById(Long id) {
        TreatmentPlan plan = treatmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan not found with ID: " + id));
        return mapToResponse(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentPlanResponse getTreatmentPlanByNumber(String planNumber) {
        TreatmentPlan plan = treatmentPlanRepository.findByPlanNumber(planNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan not found with number: " + planNumber));
        return mapToResponse(plan);
    }

    @Override
    public TreatmentPlanResponse updateTreatmentPlan(Long id, TreatmentPlanRequest request) {
        TreatmentPlan plan = treatmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan not found with ID: " + id));

        if (request.getTitle() != null) plan.setTitle(request.getTitle().trim());
        if (request.getStatus() != null) plan.setStatus(request.getStatus());
        if (request.getNotes() != null) plan.setNotes(request.getNotes());

        TreatmentPlan updated = treatmentPlanRepository.save(plan);
        auditService.logAction("DENTIST", "UPDATE", "TREATMENT_PLAN", updated.getPlanNumber(),
                "Updated treatment plan " + updated.getPlanNumber());

        return mapToResponse(updated);
    }

    @Override
    public TreatmentPlanResponse updateTreatmentPlanStatus(Long id, TreatmentPlanStatus status) {
        TreatmentPlan plan = treatmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan not found with ID: " + id));

        plan.setStatus(status);
        TreatmentPlan updated = treatmentPlanRepository.save(plan);
        auditService.logAction("DENTIST", "UPDATE_STATUS", "TREATMENT_PLAN", updated.getPlanNumber(),
                "Changed status to " + status);

        return mapToResponse(updated);
    }

    @Override
    public TreatmentPlanItemResponse addItemToPlan(Long planId, TreatmentPlanItemRequest itemRequest) {
        TreatmentPlan plan = treatmentPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan not found with ID: " + planId));

        Treatment treatment = treatmentRepository.findById(itemRequest.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + itemRequest.getTreatmentId()));

        int qty = itemRequest.getQuantity() != null && itemRequest.getQuantity() > 0 ? itemRequest.getQuantity() : 1;
        BigDecimal unitCost = itemRequest.getUnitCost() != null ? itemRequest.getUnitCost() : treatment.getTreatmentCost();
        BigDecimal estCost = unitCost.multiply(BigDecimal.valueOf(qty));

        TreatmentPlanItem item = new TreatmentPlanItem(
                plan,
                treatment,
                itemRequest.getToothNumber(),
                qty,
                unitCost,
                estCost,
                BigDecimal.ZERO,
                itemRequest.getStatus() != null ? itemRequest.getStatus() : TreatmentItemStatus.PENDING,
                itemRequest.getNotes()
        );

        TreatmentPlanItem saved = treatmentPlanItemRepository.save(item);

        // Recalculate plan totals
        recalculatePlanTotals(plan);

        return mapToItemResponse(saved);
    }

    @Override
    public TreatmentPlanItemResponse updateItemStatus(Long itemId, TreatmentItemStatus status) {
        TreatmentPlanItem item = treatmentPlanItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan item not found with ID: " + itemId));
        item.setStatus(status);
        TreatmentPlanItem saved = treatmentPlanItemRepository.save(item);
        return mapToItemResponse(saved);
    }

    @Override
    public void removeItemFromPlan(Long planId, Long itemId) {
        TreatmentPlan plan = treatmentPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan not found with ID: " + planId));

        TreatmentPlanItem item = treatmentPlanItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan item not found with ID: " + itemId));

        treatmentPlanItemRepository.delete(item);
        recalculatePlanTotals(plan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentPlanResponse> getTreatmentPlansByPatientId(Long patientId) {
        return treatmentPlanRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentPlanResponse> getTreatmentPlansByDentistId(Long dentistId) {
        return treatmentPlanRepository.findByDentistIdOrderByCreatedAtDesc(dentistId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentPlanResponse> getTreatmentPlansByStatus(TreatmentPlanStatus status) {
        return treatmentPlanRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteTreatmentPlan(Long id) {
        TreatmentPlan plan = treatmentPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment plan not found with ID: " + id));
        treatmentPlanRepository.delete(plan);
        auditService.logAction("ADMIN", "DELETE", "TREATMENT_PLAN", plan.getPlanNumber(),
                "Deleted treatment plan " + plan.getPlanNumber());
    }

    private void recalculatePlanTotals(TreatmentPlan plan) {
        List<TreatmentPlanItem> items = treatmentPlanItemRepository.findByTreatmentPlanId(plan.getId());
        BigDecimal totalEst = BigDecimal.ZERO;
        BigDecimal totalAct = BigDecimal.ZERO;

        for (TreatmentPlanItem it : items) {
            if (it.getEstimatedCost() != null) totalEst = totalEst.add(it.getEstimatedCost());
            if (it.getActualCost() != null) totalAct = totalAct.add(it.getActualCost());
        }

        plan.setEstimatedCost(totalEst);
        plan.setActualCost(totalAct);
        treatmentPlanRepository.save(plan);
    }

    private TreatmentPlanResponse mapToResponse(TreatmentPlan p) {
        TreatmentPlanResponse resp = new TreatmentPlanResponse();
        resp.setId(p.getId());
        resp.setPlanNumber(p.getPlanNumber());

        if (p.getPatient() != null) {
            resp.setPatientId(p.getPatient().getId());
            resp.setPatientNumber(p.getPatient().getPatientNumber());
            resp.setPatientName(p.getPatient().getFullName());
        }

        if (p.getDentist() != null) {
            resp.setDentistId(p.getDentist().getId());
            resp.setDentistName(p.getDentist().getName());
        }

        resp.setTitle(p.getTitle());
        resp.setStatus(p.getStatus());
        resp.setEstimatedCost(p.getEstimatedCost());
        resp.setActualCost(p.getActualCost());
        resp.setNotes(p.getNotes());
        resp.setCreatedAt(p.getCreatedAt());
        resp.setUpdatedAt(p.getUpdatedAt());

        List<TreatmentPlanItem> items = treatmentPlanItemRepository.findByTreatmentPlanId(p.getId());
        resp.setItems(items.stream().map(this::mapToItemResponse).collect(Collectors.toList()));

        return resp;
    }

    private TreatmentPlanItemResponse mapToItemResponse(TreatmentPlanItem it) {
        TreatmentPlanItemResponse resp = new TreatmentPlanItemResponse();
        resp.setId(it.getId());
        resp.setTreatmentPlanId(it.getTreatmentPlan().getId());

        if (it.getTreatment() != null) {
            resp.setTreatmentId(it.getTreatment().getId());
            resp.setTreatmentCode(it.getTreatment().getTreatmentCode());
            resp.setTreatmentName(it.getTreatment().getTreatmentName());
        }

        resp.setToothNumber(it.getToothNumber());
        resp.setQuantity(it.getQuantity());
        resp.setUnitCost(it.getUnitCost());
        resp.setEstimatedCost(it.getEstimatedCost());
        resp.setActualCost(it.getActualCost());
        resp.setStatus(it.getStatus());
        resp.setNotes(it.getNotes());
        resp.setCreatedAt(it.getCreatedAt());
        resp.setUpdatedAt(it.getUpdatedAt());
        return resp;
    }
}
