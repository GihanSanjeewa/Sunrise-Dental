package com.sunrise.dental.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "diagnoses")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinical_record_id")
    private ClinicalRecord clinicalRecord;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @Column(name = "diagnosis_code", length = 30)
    private String diagnosisCode;

    @Column(name = "diagnosis_name", nullable = false, length = 200)
    private String diagnosisName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "diagnosed_date", nullable = false)
    private LocalDate diagnosedDate;

    @Column(name = "related_teeth", length = 100)
    private String relatedTeeth;

    @Column(nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, RESOLVED, CHRONIC

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Diagnosis() {
    }

    public Diagnosis(ClinicalRecord clinicalRecord, Patient patient, Dentist dentist, String diagnosisCode,
                     String diagnosisName, String notes, LocalDate diagnosedDate, String relatedTeeth, String status) {
        this.clinicalRecord = clinicalRecord;
        this.patient = patient;
        this.dentist = dentist;
        this.diagnosisCode = diagnosisCode;
        this.diagnosisName = diagnosisName;
        this.notes = notes;
        this.diagnosedDate = diagnosedDate;
        this.relatedTeeth = relatedTeeth;
        this.status = status != null ? status : "ACTIVE";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.diagnosedDate == null) {
            this.diagnosedDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = "ACTIVE";
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

    public ClinicalRecord getClinicalRecord() {
        return clinicalRecord;
    }

    public void setClinicalRecord(ClinicalRecord clinicalRecord) {
        this.clinicalRecord = clinicalRecord;
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

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisName() {
        return diagnosisName;
    }

    public void setDiagnosisName(String diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getDiagnosedDate() {
        return diagnosedDate;
    }

    public void setDiagnosedDate(LocalDate diagnosedDate) {
        this.diagnosedDate = diagnosedDate;
    }

    public String getRelatedTeeth() {
        return relatedTeeth;
    }

    public void setRelatedTeeth(String relatedTeeth) {
        this.relatedTeeth = relatedTeeth;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
