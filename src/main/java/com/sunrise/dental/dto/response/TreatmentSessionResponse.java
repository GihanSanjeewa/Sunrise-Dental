package com.sunrise.dental.dto.response;

import com.sunrise.dental.enums.SessionStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TreatmentSessionResponse {

    private Long id;
    private Long treatmentPlanId;
    private String treatmentPlanNumber;
    private String treatmentPlanTitle;
    private Long patientId;
    private String patientNumber;
    private String patientName;
    private Long dentistId;
    private String dentistName;
    private Long appointmentId;
    private String appointmentNumber;
    private Integer sessionNumber;
    private LocalDate sessionDate;
    private String completedTreatment;
    private String relatedTeeth;
    private SessionStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TreatmentSessionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTreatmentPlanId() {
        return treatmentPlanId;
    }

    public void setTreatmentPlanId(Long treatmentPlanId) {
        this.treatmentPlanId = treatmentPlanId;
    }

    public String getTreatmentPlanNumber() {
        return treatmentPlanNumber;
    }

    public void setTreatmentPlanNumber(String treatmentPlanNumber) {
        this.treatmentPlanNumber = treatmentPlanNumber;
    }

    public String getTreatmentPlanTitle() {
        return treatmentPlanTitle;
    }

    public void setTreatmentPlanTitle(String treatmentPlanTitle) {
        this.treatmentPlanTitle = treatmentPlanTitle;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public String getDentistName() {
        return dentistName;
    }

    public void setDentistName(String dentistName) {
        this.dentistName = dentistName;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
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
