package com.sunrise.dental.dto.response;

import com.sunrise.dental.enums.ToothCondition;
import java.time.LocalDate;
import java.util.List;

public class ToothRecordResponse {

    private Long id;
    private Long patientId;
    private Integer toothNumber;
    private ToothCondition condition;
    private String status;
    private String notes;
    private LocalDate lastTreatmentDate;
    private List<ToothHistoryResponse> history;

    public ToothRecordResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ToothHistoryResponse> getHistory() {
        return history;
    }

    public void setHistory(List<ToothHistoryResponse> history) {
        this.history = history;
    }
}
