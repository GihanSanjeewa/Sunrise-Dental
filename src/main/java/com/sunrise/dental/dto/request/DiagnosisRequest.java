package com.sunrise.dental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class DiagnosisRequest {

    private Long clinicalRecordId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @NotNull(message = "Dentist ID is required")
    private Long dentistId;

    private String diagnosisCode;

    @NotBlank(message = "Diagnosis name is required")
    private String diagnosisName;

    private String notes;

    private LocalDate diagnosedDate;

    private String relatedTeeth;

    private String status; // ACTIVE, RESOLVED, CHRONIC

    public DiagnosisRequest() {
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

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
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
}
