package com.sunrise.dental.dto.request;

import com.sunrise.dental.enums.ToothCondition;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ToothRecordRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Tooth number is required")
    @Min(value = 11, message = "Invalid FDI tooth number")
    @Max(value = 48, message = "Invalid FDI tooth number")
    private Integer toothNumber;

    @NotNull(message = "Tooth condition is required")
    private ToothCondition condition;

    private String status;

    private String notes;

    private LocalDate lastTreatmentDate;

    public ToothRecordRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
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
}
