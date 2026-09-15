package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.TreatmentRequest;
import com.sunrise.dental.dto.response.TreatmentResponse;
import com.sunrise.dental.service.TreatmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatments")
@Tag(name = "Treatment Catalog Management", description = "Clinical treatments, dynamic database-driven pricing, and procedure descriptions")
public class TreatmentController {

    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @PostMapping
    @Operation(summary = "Add a new dental treatment procedure with custom DB prices (Admin only)")
    public ResponseEntity<TreatmentResponse> addTreatment(@Valid @RequestBody TreatmentRequest request) {
        TreatmentResponse response = treatmentService.addTreatment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all treatments in catalog")
    public ResponseEntity<List<TreatmentResponse>> getAllTreatments() {
        return ResponseEntity.ok(treatmentService.getAllTreatments());
    }

    @GetMapping("/active")
    @Operation(summary = "List only active treatments available for appointment bookings")
    public ResponseEntity<List<TreatmentResponse>> getActiveTreatments() {
        return ResponseEntity.ok(treatmentService.getActiveTreatments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get treatment details by database ID")
    public ResponseEntity<TreatmentResponse> getTreatmentById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentService.getTreatmentById(id));
    }

    @GetMapping("/code/{treatmentCode}")
    @Operation(summary = "Get treatment by unique code (e.g. TRT-001)")
    public ResponseEntity<TreatmentResponse> getTreatmentByCode(@PathVariable String treatmentCode) {
        return ResponseEntity.ok(treatmentService.getTreatmentByCode(treatmentCode));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update treatment price, consultation fee, or description")
    public ResponseEntity<TreatmentResponse> updateTreatment(@PathVariable Long id, @Valid @RequestBody TreatmentRequest request) {
        return ResponseEntity.ok(treatmentService.updateTreatment(id, request));
    }
}
