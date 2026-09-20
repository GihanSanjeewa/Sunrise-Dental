package com.sunrise.dental.dto.request;

import com.sunrise.dental.enums.SessionStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class TreatmentSessionRequest {

    @NotNull(message = "Treatment plan ID is required")
    private Long treatmentPlanId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Dentist ID is required")
    private Long dentistId;

    private Long appointmentId;

    private Integer sessionNumber;

    private LocalDate sessionDate;

    private String completedTreatment;

    private String relatedTeeth;

    private SessionStatus status;

    private String notes;

    public TreatmentSessionRequest() {
    }

    public Long getTreatmentPlanId() {
        return treatmentPlanId;
    }

    public void setTreatmentPlanId(Long treatmentPlanId) {
        this.treatmentPlanId = treatmentPlanId;
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

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public LocalDate getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDate sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getCompletedTreatment() {
        return completedTreatment;
    }

    public void setCompletedTreatment(String completedTreatment) {
        this.completedTreatment = completedTreatment;
    }

    public String getRelatedTeeth() {
        return relatedTeeth;
    }

    public void setRelatedTeeth(String relatedTeeth) {
        this.relatedTeeth = relatedTeeth;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
