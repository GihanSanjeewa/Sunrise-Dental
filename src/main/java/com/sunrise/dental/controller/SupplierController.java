package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.SupplierRequest;
import com.sunrise.dental.dto.response.SupplierResponse;
import com.sunrise.dental.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suppliers")
@Tag(name = "Supplier Management", description = "Dental supplies vendors, contact details, and supplier codes")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @Operation(summary = "Register a new dental supply vendor")
    public ResponseEntity<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        SupplierResponse response = supplierService.createSupplier(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all suppliers")
    public ResponseEntity<List<SupplierResponse>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/active")
    @Operation(summary = "List active suppliers")
    public ResponseEntity<List<SupplierResponse>> getActiveSuppliers() {
        return ResponseEntity.ok(supplierService.getActiveSuppliers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier details by ID")
    public ResponseEntity<SupplierResponse> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier contact and address information")
    public ResponseEntity<SupplierResponse> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate supplier")
    public ResponseEntity<Map<String, String>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(Map.of("message", "Supplier deactivated successfully."));
    }
}
