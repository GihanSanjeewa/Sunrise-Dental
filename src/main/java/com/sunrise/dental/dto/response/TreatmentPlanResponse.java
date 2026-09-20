package com.sunrise.dental.dto.response;

import com.sunrise.dental.enums.TreatmentPlanStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TreatmentPlanResponse {

    private Long id;
    private String planNumber;
    private Long patientId;
    private String patientNumber;
    private String patientName;
    private Long dentistId;
    private String dentistName;
    private String title;
    private TreatmentPlanStatus status;
    private BigDecimal estimatedCost;
    private BigDecimal actualCost;
    private String notes;
    private List<TreatmentPlanItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TreatmentPlanResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlanNumber() {
        return planNumber;
    }

    public void setPlanNumber(String planNumber) {
        this.planNumber = planNumber;
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

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<TreatmentPlanItemResponse> getItems() {
        return items;
    }

    public void setItems(List<TreatmentPlanItemResponse> items) {
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
