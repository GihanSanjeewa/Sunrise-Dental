package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.BillRequest;
import com.sunrise.dental.dto.response.BillResponse;
import com.sunrise.dental.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
@Tag(name = "Billing Management", description = "Dynamic bill generation, fee calculation, discounts, and tax computation")
public class BillController {

    private final BillingService billingService;

    public BillController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    @Operation(summary = "Generate and persist invoice bill for an appointment")
    public ResponseEntity<BillResponse> generateBill(@Valid @RequestBody BillRequest request) {
        BillResponse response = billingService.generateBill(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate and preview bill without persisting (useful for front-desk estimates)")
    public ResponseEntity<BillResponse> calculatePreview(
            @RequestParam Long appointmentId,
            @RequestParam(required = false, defaultValue = "0") BigDecimal discountPercentage,
            @RequestParam(required = false, defaultValue = "0") BigDecimal taxPercentage) {
        return ResponseEntity.ok(billingService.calculateBill(appointmentId, discountPercentage, taxPercentage));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bill details by database ID")
    public ResponseEntity<BillResponse> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillById(id));
    }

    @GetMapping("/number/{billNumber}")
    @Operation(summary = "Get bill by unique bill number (e.g. BILL-2026-000001)")
    public ResponseEntity<BillResponse> getBillByNumber(@PathVariable String billNumber) {
        return ResponseEntity.ok(billingService.getBillByNumber(billNumber));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get bill associated with an appointment ID")
    public ResponseEntity<BillResponse> getBillByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(billingService.getBillByAppointmentId(appointmentId));
    }

    @GetMapping
    @Operation(summary = "List all generated bills")
    public ResponseEntity<List<BillResponse>> getAllBills() {
        return ResponseEntity.ok(billingService.getAllBills());
    }

    @GetMapping("/pending")
    @Operation(summary = "List all bills with PENDING payment status")
    public ResponseEntity<List<BillResponse>> getPendingBills() {
        return ResponseEntity.ok(billingService.getPendingBills());
    }
}
