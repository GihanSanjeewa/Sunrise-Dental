package com.sunrise.dental.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ToothHistoryResponse {

    private Long id;
    private Long dentalToothId;
    private Integer toothNumber;
    private Long clinicalRecordId;
    private String treatmentName;
    private LocalDate procedureDate;
    private String notes;
    private LocalDateTime createdAt;

    public ToothHistoryResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDentalToothId() {
        return dentalToothId;
    }

    public void setDentalToothId(Long dentalToothId) {
        this.dentalToothId = dentalToothId;
    }

    public Integer getToothNumber() {
        return toothNumber;
    }

    public void setToothNumber(Integer toothNumber) {
        this.toothNumber = toothNumber;
    }

    public Long getClinicalRecordId() {
        return clinicalRecordId;
    }

    public void setClinicalRecordId(Long clinicalRecordId) {
        this.clinicalRecordId = clinicalRecordId;
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
