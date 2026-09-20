package com.sunrise.dental.dto.response;

import java.util.List;

public class NoShowRiskResponse {

    private Long appointmentId;
    private String appointmentNumber;
    private String patientName;
    private Integer riskScore; // 0 to 100 percentage
    private String riskLevel;  // LOW, MEDIUM, HIGH
    private List<String> contributingFactors;
    private String disclaimer;

    public NoShowRiskResponse() {
        this.disclaimer = "Decision-support estimate only. This analysis is an educational AI/statistical projection and must not be used to automatically cancel appointments or discriminate against patients.";
    }

    public NoShowRiskResponse(Long appointmentId, String appointmentNumber, String patientName,
                              Integer riskScore, String riskLevel, List<String> contributingFactors) {
        this();
        this.appointmentId = appointmentId;
        this.appointmentNumber = appointmentNumber;
        this.patientName = patientName;
        this.riskScore = riskScore;
        this.riskLevel = riskLevel;
        this.contributingFactors = contributingFactors;
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public List<String> getContributingFactors() {
        return contributingFactors;
    }

    public void setContributingFactors(List<String> contributingFactors) {
        this.contributingFactors = contributingFactors;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
