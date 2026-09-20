package com.sunrise.dental.dto.response;

import java.util.List;

public class DentalChartResponse {

    private Long patientId;
    private String patientNumber;
    private String patientName;
    private List<ToothRecordResponse> upperRight; // 18-11
    private List<ToothRecordResponse> upperLeft;  // 21-28
    private List<ToothRecordResponse> lowerLeft;  // 38-31
    private List<ToothRecordResponse> lowerRight; // 41-48
    private List<ToothRecordResponse> allTeeth;

    public DentalChartResponse() {
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

    public List<ToothRecordResponse> getUpperRight() {
        return upperRight;
    }

    public void setUpperRight(List<ToothRecordResponse> upperRight) {
        this.upperRight = upperRight;
    }

    public List<ToothRecordResponse> getUpperLeft() {
        return upperLeft;
    }

    public void setUpperLeft(List<ToothRecordResponse> upperLeft) {
        this.upperLeft = upperLeft;
    }

    public List<ToothRecordResponse> getLowerLeft() {
        return lowerLeft;
    }

    public void setLowerLeft(List<ToothRecordResponse> lowerLeft) {
        this.lowerLeft = lowerLeft;
    }

    public List<ToothRecordResponse> getLowerRight() {
        return lowerRight;
    }

    public void setLowerRight(List<ToothRecordResponse> lowerRight) {
        this.lowerRight = lowerRight;
    }

    public List<ToothRecordResponse> getAllTeeth() {
        return allTeeth;
    }

    public void setAllTeeth(List<ToothRecordResponse> allTeeth) {
        this.allTeeth = allTeeth;
    }
}
