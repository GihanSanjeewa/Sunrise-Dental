package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.PurchaseOrderRequest;
import com.sunrise.dental.dto.response.PurchaseOrderResponse;
import com.sunrise.dental.enums.PurchaseOrderStatus;
import com.sunrise.dental.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@Tag(name = "Purchase Orders", description = "Purchase orders for inventory replenishment and automatic goods receipt")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    @Operation(summary = "Create a new purchase order for supplier items")
    public ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request,
            Authentication authentication) {
        String performedBy = authentication != null ? authentication.getName() : "SYSTEM";
        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(request, performedBy);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all purchase orders")
    public ResponseEntity<List<PurchaseOrderResponse>> getAllPurchaseOrders() {
        return ResponseEntity.ok(purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order details and items by ID")
    public ResponseEntity<PurchaseOrderResponse> getPurchaseOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseOrderService.getPurchaseOrderById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update PO status. Setting to RECEIVED automatically increments inventory stock and records transactions")
    public ResponseEntity<PurchaseOrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam PurchaseOrderStatus status,
            Authentication authentication) {
        String performedBy = authentication != null ? authentication.getName() : "SYSTEM";
        return ResponseEntity.ok(purchaseOrderService.updatePurchaseOrderStatus(id, status, performedBy));
    }
}
