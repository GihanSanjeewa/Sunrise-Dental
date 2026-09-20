package com.sunrise.dental.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiagnosisResponse {

    private Long id;
    private Long clinicalRecordId;
    private Long patientId;
    private String patientNumber;
    private String patientName;
    private Long dentistId;
    private String dentistName;
    private String diagnosisCode;
    private String diagnosisName;
    private String notes;
    private LocalDate diagnosedDate;
    private String relatedTeeth;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DiagnosisResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClinicalRecordId() {
        return clinicalRecordId;
    }

    public void setClinicalRecordId(Long clinicalRecordId) {
        this.clinicalRecordId = clinicalRecordId;
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
