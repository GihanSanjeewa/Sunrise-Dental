package com.sunrise.dental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ToothHistoryRequest {

    @NotNull(message = "Dental tooth ID is required")
    private Long dentalToothId;

    private Long clinicalRecordId;

    @NotBlank(message = "Treatment name is required")
    private String treatmentName;

    private LocalDate procedureDate;

    private String notes;

    public ToothHistoryRequest() {
    }

    public Long getDentalToothId() {
        return dentalToothId;
    }

    public void setDentalToothId(Long dentalToothId) {
        this.dentalToothId = dentalToothId;
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
}
