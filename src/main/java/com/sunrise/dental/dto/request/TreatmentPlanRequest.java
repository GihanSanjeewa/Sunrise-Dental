package com.sunrise.dental.dto.request;

import com.sunrise.dental.enums.TreatmentPlanStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class TreatmentPlanRequest {

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Dentist ID is required")
    private Long dentistId;

    @NotBlank(message = "Title is required")
    private String title;

    private TreatmentPlanStatus status;

    private String notes;

    private List<TreatmentPlanItemRequest> items;

    public TreatmentPlanRequest() {
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TreatmentPlanItemRequest> getItems() {
        return items;
    }

    public void setItems(List<TreatmentPlanItemRequest> items) {
        this.items = items;
    }
}
