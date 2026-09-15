package com.sunrise.dental.dto.request;

import com.sunrise.dental.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class PaymentRequest {

    @NotNull(message = "Bill ID is required.")
    private Long billId;

    @NotNull(message = "Payment amount is required.")
    @Positive(message = "Payment amount must be greater than zero.")
    private BigDecimal amountPaid;

    @NotNull(message = "Payment method is required.")
    private PaymentMethod paymentMethod;

    private String notes;

    public PaymentRequest() {
    }

    public PaymentRequest(Long billId, BigDecimal amountPaid, PaymentMethod paymentMethod, String notes) {
        this.billId = billId;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
