package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.TreatmentRequest;
import com.sunrise.dental.dto.response.TreatmentResponse;
import com.sunrise.dental.entity.Treatment;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.TreatmentRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.TreatmentService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final AuditService auditService;

    public TreatmentServiceImpl(TreatmentRepository treatmentRepository, AuditService auditService) {
        this.treatmentRepository = treatmentRepository;
        this.auditService = auditService;
    }

    @Override
    public TreatmentResponse addTreatment(TreatmentRequest request) {
        validateTreatmentRequest(request);

        Long maxId = treatmentRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String treatmentCode = IdSequenceGenerator.generateTreatmentCode(nextId);

        while (treatmentRepository.existsByTreatmentCode(treatmentCode)) {
            nextId++;
            treatmentCode = IdSequenceGenerator.generateTreatmentCode(nextId);
        }

        Treatment treatment = new Treatment(
                treatmentCode,
                request.getTreatmentName().trim(),
                request.getDescription() != null ? request.getDescription().trim() : null,
                request.getTreatmentCost(),
                request.getConsultationFee(),
                request.getStatus() != null ? request.getStatus().trim().toUpperCase() : "ACTIVE"
        );

        Treatment saved = treatmentRepository.save(treatment);
        auditService.logAction("ADMIN", "ADD_TREATMENT", "TREATMENT", saved.getTreatmentCode(),
                "Created treatment " + saved.getTreatmentName() + " (Cost: " + saved.getTreatmentCost() + ", Fee: " + saved.getConsultationFee() + ")");

        return mapToResponse(saved);
    }

    @Override
    public TreatmentResponse updateTreatment(Long id, TreatmentRequest request) {
        validateTreatmentRequest(request);

        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + id));

        treatment.setTreatmentName(request.getTreatmentName().trim());
        treatment.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        treatment.setTreatmentCost(request.getTreatmentCost());
        treatment.setConsultationFee(request.getConsultationFee());
        if (request.getStatus() != null) {
            treatment.setStatus(request.getStatus().trim().toUpperCase());
        }

        Treatment updated = treatmentRepository.save(treatment);
        auditService.logAction("ADMIN", "UPDATE_TREATMENT", "TREATMENT", updated.getTreatmentCode(),
                "Updated treatment " + updated.getTreatmentName() + " (Cost: " + updated.getTreatmentCost() + ", Fee: " + updated.getConsultationFee() + ")");

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentResponse getTreatmentById(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with ID: " + id));
        return mapToResponse(treatment);
    }

    @Override
    @Transactional(readOnly = true)
    public TreatmentResponse getTreatmentByCode(String code) {
        Treatment treatment = treatmentRepository.findByTreatmentCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with code: " + code));
        return mapToResponse(treatment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponse> getAllTreatments() {
        return treatmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreatmentResponse> getActiveTreatments() {
        return treatmentRepository.findByStatus("ACTIVE").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void validateTreatmentRequest(TreatmentRequest request) {
        if (request.getTreatmentName() == null || request.getTreatmentName().trim().isEmpty()) {
            throw new ValidationException("Treatment name cannot be empty.");
        }
        if (request.getTreatmentCost() == null || request.getTreatmentCost().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Treatment cost must be zero or positive.");
        }
        if (request.getConsultationFee() == null || request.getConsultationFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Consultation fee must be zero or positive.");
        }
    }

    private TreatmentResponse mapToResponse(Treatment t) {
        return new TreatmentResponse(
                t.getId(),
                t.getTreatmentCode(),
                t.getTreatmentName(),
                t.getDescription(),
                t.getTreatmentCost(),
                t.getConsultationFee(),
                t.getStatus(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
