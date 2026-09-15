package com.sunrise.dental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class TreatmentRequest {

    @NotBlank(message = "Treatment name is required.")
    private String treatmentName;

    private String description;

    @NotNull(message = "Treatment cost is required.")
    @PositiveOrZero(message = "Treatment cost must be zero or positive.")
    private BigDecimal treatmentCost;

    @NotNull(message = "Consultation fee is required.")
    @PositiveOrZero(message = "Consultation fee must be zero or positive.")
    private BigDecimal consultationFee;

    private String status = "ACTIVE";

    public TreatmentRequest() {
    }

    public TreatmentRequest(String treatmentName, String description, BigDecimal treatmentCost, BigDecimal consultationFee, String status) {
        this.treatmentName = treatmentName;
        this.description = description;
        this.treatmentCost = treatmentCost;
        this.consultationFee = consultationFee;
        this.status = status != null ? status : "ACTIVE";
    }

    public String getTreatmentName() {
        return treatmentName;
    }

    public void setTreatmentName(String treatmentName) {
        this.treatmentName = treatmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTreatmentCost() {
        return treatmentCost;
    }

    public void setTreatmentCost(BigDecimal treatmentCost) {
        this.treatmentCost = treatmentCost;
    }

    public BigDecimal getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(BigDecimal consultationFee) {
        this.consultationFee = consultationFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
