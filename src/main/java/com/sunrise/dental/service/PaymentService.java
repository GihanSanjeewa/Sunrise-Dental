package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.PaymentRequest;
import com.sunrise.dental.dto.response.PaymentResponse;
import com.sunrise.dental.dto.response.ReceiptResponse;

import java.util.List;

public interface PaymentService {
    ReceiptResponse processPayment(PaymentRequest request);
    ReceiptResponse getReceiptByBillId(Long billId);
    ReceiptResponse getReceiptByPaymentId(Long paymentId);
    List<PaymentResponse> getPaymentsForBill(Long billId);
    List<PaymentResponse> getAllPayments();
}
