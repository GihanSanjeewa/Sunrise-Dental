package com.sunrise.dental.entity;

import com.sunrise.dental.enums.ToothCondition;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dental_teeth", uniqueConstraints = {
    @UniqueConstraint(name = "uk_patient_tooth", columnNames = {"patient_id", "tooth_number"})
})
public class DentalToothRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "tooth_number", nullable = false)
    private Integer toothNumber; // FDI numbering 11-48

    @Enumerated(EnumType.STRING)
    @Column(name = "tooth_condition", nullable = false, length = 30)
    private ToothCondition condition = ToothCondition.HEALTHY;

    @Column(nullable = false, length = 50)
    private String status = "SOUND";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "last_treatment_date")
    private LocalDate lastTreatmentDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public DentalToothRecord() {
    }

    public DentalToothRecord(Patient patient, Integer toothNumber, ToothCondition condition, String status, String notes, LocalDate lastTreatmentDate) {
        this.patient = patient;
        this.toothNumber = toothNumber;
        this.condition = condition != null ? condition : ToothCondition.HEALTHY;
        this.status = status != null ? status : "SOUND";
        this.notes = notes;
        this.lastTreatmentDate = lastTreatmentDate;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.condition == null) {
            this.condition = ToothCondition.HEALTHY;
        }
        if (this.status == null) {
            this.status = "SOUND";
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Integer getToothNumber() {
        return toothNumber;
    }

    public void setToothNumber(Integer toothNumber) {
        this.toothNumber = toothNumber;
    }

    public ToothCondition getCondition() {
        return condition;
    }

    public void setCondition(ToothCondition condition) {
        this.condition = condition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDate getLastTreatmentDate() {
        return lastTreatmentDate;
    }

    public void setLastTreatmentDate(LocalDate lastTreatmentDate) {
        this.lastTreatmentDate = lastTreatmentDate;
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
