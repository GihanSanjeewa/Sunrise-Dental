package com.sunrise.dental.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionResponse {

    private Long id;
    private String prescriptionNumber;
    private Long patientId;
    private String patientNumber;
    private String patientName;
    private Integer patientAge;
    private String patientGender;
    private Long dentistId;
    private String dentistName;
    private String dentistSpecialization;
    private Long clinicalRecordId;
    private LocalDate prescriptionDate;
    private String notes;
    private List<PrescriptionItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PrescriptionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }

    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
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

    public Integer getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(Integer patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
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

    public String getDentistSpecialization() {
        return dentistSpecialization;
    }

    public void setDentistSpecialization(String dentistSpecialization) {
        this.dentistSpecialization = dentistSpecialization;
    }

    public Long getClinicalRecordId() {
        return clinicalRecordId;
    }

    public void setClinicalRecordId(Long clinicalRecordId) {
        this.clinicalRecordId = clinicalRecordId;
    }

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PrescriptionItemResponse> getItems() {
        return items;
    }

    public void setItems(List<PrescriptionItemResponse> items) {
        this.items = items;
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
