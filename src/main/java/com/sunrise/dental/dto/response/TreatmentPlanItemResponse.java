package com.sunrise.dental.dto.response;

import com.sunrise.dental.enums.TreatmentItemStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TreatmentPlanItemResponse {

    private Long id;
    private Long treatmentPlanId;
    private Long treatmentId;
    private String treatmentCode;
    private String treatmentName;
    private String toothNumber;
    private Integer quantity;
    private BigDecimal unitCost;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private TreatmentItemStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TreatmentPlanItemResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTreatmentPlanId() {
        return treatmentPlanId;
    }

    public void setTreatmentPlanId(Long treatmentPlanId) {
        this.treatmentPlanId = treatmentPlanId;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
    }

    public String getTreatmentCode() {
        return treatmentCode;
    }

    public void setTreatmentCode(String treatmentCode) {
        this.treatmentCode = treatmentCode;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getToothNumber() {
        return toothNumber;
    }

    public void setToothNumber(String toothNumber) {
        this.toothNumber = toothNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public TreatmentItemStatus getStatus() {
        return status;
    }

    public void setStatus(TreatmentItemStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
