package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.PaymentRequest;
import com.sunrise.dental.dto.response.PaymentResponse;
import com.sunrise.dental.dto.response.ReceiptResponse;
import com.sunrise.dental.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment & Receipt Management", description = "Payment recording via Strategy Pattern and printable receipt generation via Factory Pattern")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Record payment for a bill (CASH, CARD, BANK_TRANSFER) and return printable official receipt")
    public ResponseEntity<ReceiptResponse> recordPayment(@Valid @RequestBody PaymentRequest request) {
        ReceiptResponse receipt = paymentService.processPayment(request);
        return new ResponseEntity<>(receipt, HttpStatus.CREATED);
    }

    @GetMapping("/receipt/bill/{billId}")
    @Operation(summary = "Get official printable receipt for a bill")
    public ResponseEntity<ReceiptResponse> getReceiptByBillId(@PathVariable Long billId) {
        return ResponseEntity.ok(paymentService.getReceiptByBillId(billId));
    }

    @GetMapping("/receipt/{paymentId}")
    @Operation(summary = "Get official printable receipt by payment transaction ID")
    public ResponseEntity<ReceiptResponse> getReceiptByPaymentId(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getReceiptByPaymentId(paymentId));
    }

    @GetMapping("/bill/{billId}")
    @Operation(summary = "Get payment transaction history for a specific bill")
    public ResponseEntity<List<PaymentResponse>> getPaymentsForBill(@PathVariable Long billId) {
        return ResponseEntity.ok(paymentService.getPaymentsForBill(billId));
    }

    @GetMapping
    @Operation(summary = "List all payment transactions recorded in system")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
}
