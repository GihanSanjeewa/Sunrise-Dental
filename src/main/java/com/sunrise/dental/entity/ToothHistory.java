package com.sunrise.dental.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tooth_history")
public class ToothHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dental_tooth_id", nullable = false)
    private DentalToothRecord dentalTooth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_record_id")
    private ClinicalRecord clinicalRecord;

    @Column(name = "treatment_name", nullable = false, length = 120)
    private String treatmentName;

    @Column(name = "procedure_date", nullable = false)
    private LocalDate procedureDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ToothHistory() {
    }

    public ToothHistory(DentalToothRecord dentalTooth, ClinicalRecord clinicalRecord, String treatmentName, LocalDate procedureDate, String notes) {
        this.dentalTooth = dentalTooth;
        this.clinicalRecord = clinicalRecord;
        this.treatmentName = treatmentName;
        this.procedureDate = procedureDate;
        this.notes = notes;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.procedureDate == null) {
            this.procedureDate = LocalDate.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DentalToothRecord getDentalTooth() {
        return dentalTooth;
    }

    public void setDentalTooth(DentalToothRecord dentalTooth) {
        this.dentalTooth = dentalTooth;
    }

    public ClinicalRecord getClinicalRecord() {
        return clinicalRecord;
    }

    public void setClinicalRecord(ClinicalRecord clinicalRecord) {
        this.clinicalRecord = clinicalRecord;
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public LocalDate getProcedureDate() {
        return procedureDate;
    }

    public void setProcedureDate(LocalDate procedureDate) {
        this.procedureDate = procedureDate;
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
}
