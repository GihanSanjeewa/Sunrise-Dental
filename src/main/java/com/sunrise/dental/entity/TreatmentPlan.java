package com.sunrise.dental.entity;

import com.sunrise.dental.enums.TreatmentPlanStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "treatment_plans")
public class TreatmentPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_number", nullable = false, unique = true, length = 30)
    private String planNumber;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TreatmentPlanStatus status = TreatmentPlanStatus.PLANNED;

    @Column(name = "estimated_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    @Column(name = "actual_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal actualCost = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "treatmentPlan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TreatmentPlanItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TreatmentPlan() {
    }

    public TreatmentPlan(String planNumber, Patient patient, Dentist dentist, String title,
                         TreatmentPlanStatus status, BigDecimal estimatedCost, BigDecimal actualCost, String notes) {
        this.planNumber = planNumber;
        this.patient = patient;
        this.dentist = dentist;
        this.title = title;
        this.status = status != null ? status : TreatmentPlanStatus.PLANNED;
        this.estimatedCost = estimatedCost != null ? estimatedCost : BigDecimal.ZERO;
        this.actualCost = actualCost != null ? actualCost : BigDecimal.ZERO;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TreatmentPlanStatus.PLANNED;
        }
        if (this.estimatedCost == null) {
            this.estimatedCost = BigDecimal.ZERO;
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

    public String getPlanNumber() {
        return planNumber;
    }

    public void setPlanNumber(String planNumber) {
        this.planNumber = planNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Dentist getDentist() {
        return dentist;
    }

    public void setDentist(Dentist dentist) {
        this.dentist = dentist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TreatmentPlanStatus getStatus() {
        return status;
    }

    public void setStatus(TreatmentPlanStatus status) {
        this.status = status;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TreatmentPlanItem> getItems() {
        return items;
    }

    public void setItems(List<TreatmentPlanItem> items) {
        this.items = items;
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
