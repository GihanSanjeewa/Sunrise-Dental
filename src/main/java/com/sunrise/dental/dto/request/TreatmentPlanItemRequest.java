package com.sunrise.dental.dto.request;

import com.sunrise.dental.enums.TreatmentItemStatus;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TreatmentPlanItemRequest {

    @NotNull(message = "Treatment ID is required")
    private Long treatmentId;

    private String toothNumber;

    private Integer quantity;

    private BigDecimal unitCost; // If null, fetched dynamically from Treatment catalog

    private TreatmentItemStatus status;

    private String notes;

    public TreatmentPlanItemRequest() {
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
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
}
