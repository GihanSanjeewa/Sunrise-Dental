package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.InventoryItemRequest;
import com.sunrise.dental.dto.request.StockAdjustmentRequest;
import com.sunrise.dental.dto.response.InventoryItemResponse;
import com.sunrise.dental.dto.response.InventoryTransactionResponse;
import com.sunrise.dental.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "Dental consumables, stock levels, adjustments, and low-stock alerts")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/items")
    @Operation(summary = "Register a new inventory consumable item")
    public ResponseEntity<InventoryItemResponse> createItem(@Valid @RequestBody InventoryItemRequest request) {
        InventoryItemResponse response = inventoryService.createItem(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/items")
    @Operation(summary = "Get all inventory items")
    public ResponseEntity<List<InventoryItemResponse>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/items/{id}")
    @Operation(summary = "Get inventory item by ID")
    public ResponseEntity<InventoryItemResponse> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getItemById(id));
    }

    @PutMapping("/items/{id}")
    @Operation(summary = "Update inventory item details and reorder thresholds")
    public ResponseEntity<InventoryItemResponse> updateItem(@PathVariable Long id, @Valid @RequestBody InventoryItemRequest request) {
        return ResponseEntity.ok(inventoryService.updateItem(id, request));
    }

    @PostMapping("/adjust")
    @Operation(summary = "Perform manual stock adjustment (USAGE, WASTAGE, RETURN, ADJUSTMENT)")
    public ResponseEntity<InventoryItemResponse> adjustStock(
            @Valid @RequestBody StockAdjustmentRequest request,
            Authentication authentication) {
        String performedBy = authentication != null ? authentication.getName() : "SYSTEM";
        return ResponseEntity.ok(inventoryService.adjustStock(request, performedBy));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get all items currently at or below their reorder threshold")
    public ResponseEntity<List<InventoryItemResponse>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @GetMapping("/items/{id}/transactions")
    @Operation(summary = "Get stock transaction history for an inventory item")
    public ResponseEntity<List<InventoryTransactionResponse>> getItemTransactions(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getTransactionsByItemId(id));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Deactivate inventory item")
    public ResponseEntity<Map<String, String>> deleteItem(@PathVariable Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.ok(Map.of("message", "Inventory item deactivated successfully."));
    }
}
