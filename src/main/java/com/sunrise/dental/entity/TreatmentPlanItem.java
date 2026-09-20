package com.sunrise.dental.entity;

import com.sunrise.dental.enums.TreatmentItemStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "treatment_plan_items")
public class TreatmentPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_plan_id", nullable = false)
    private TreatmentPlan treatmentPlan;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @Column(name = "tooth_number", length = 30)
    private String toothNumber;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "estimated_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @Column(name = "actual_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualCost = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TreatmentItemStatus status = TreatmentItemStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TreatmentPlanItem() {
    }

    public TreatmentPlanItem(TreatmentPlan treatmentPlan, Treatment treatment, String toothNumber,
                             Integer quantity, BigDecimal unitCost, BigDecimal estimatedCost,
                             BigDecimal actualCost, TreatmentItemStatus status, String notes) {
        this.treatmentPlan = treatmentPlan;
        this.treatment = treatment;
        this.toothNumber = toothNumber;
        this.quantity = quantity != null ? quantity : 1;
        this.unitCost = unitCost;
        this.estimatedCost = estimatedCost;
        this.actualCost = actualCost != null ? actualCost : BigDecimal.ZERO;
        this.status = status != null ? status : TreatmentItemStatus.PENDING;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TreatmentItemStatus.PENDING;
        }
        if (this.quantity == null) {
            this.quantity = 1;
        }
        if (this.actualCost == null) {
            this.actualCost = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TreatmentPlan getTreatmentPlan() {
        return treatmentPlan;
    }

    public void setTreatmentPlan(TreatmentPlan treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }

    public Treatment getTreatment() {
        return treatment;
    }

    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
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
