package com.sunrise.dental.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class BillRequest {

    @NotNull(message = "Appointment ID is required.")
    private Long appointmentId;

    @PositiveOrZero(message = "Discount percentage cannot be negative.")
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @PositiveOrZero(message = "Tax percentage cannot be negative.")
    private BigDecimal taxPercentage = BigDecimal.ZERO;

    public BillRequest() {
    }

    public BillRequest(Long appointmentId, BigDecimal discountPercentage, BigDecimal taxPercentage) {
        this.appointmentId = appointmentId;
        this.discountPercentage = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
        this.taxPercentage = taxPercentage != null ? taxPercentage : BigDecimal.ZERO;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(BigDecimal taxPercentage) {
        this.taxPercentage = taxPercentage;
    }
}
