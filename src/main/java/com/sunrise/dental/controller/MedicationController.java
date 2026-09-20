package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.MedicationRequest;
import com.sunrise.dental.dto.response.MedicationResponse;
import com.sunrise.dental.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medications")
@Tag(name = "Medication Master Formulary", description = "Medication master catalog, forms, strengths, and standard instructions")
public class MedicationController {

    private final PrescriptionService prescriptionService;

    public MedicationController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    @Operation(summary = "Add new medication to formulary")
    public ResponseEntity<MedicationResponse> createMedication(@Valid @RequestBody MedicationRequest request) {
        MedicationResponse response = prescriptionService.createMedication(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all active medications in the clinic formulary")
    public ResponseEntity<List<MedicationResponse>> getAllMedications() {
        return ResponseEntity.ok(prescriptionService.getAllMedications());
    }

    @GetMapping("/search")
    @Operation(summary = "Search medications by brand name or generic name")
    public ResponseEntity<List<MedicationResponse>> searchMedications(@RequestParam(required = false, defaultValue = "") String query) {
        return ResponseEntity.ok(prescriptionService.searchMedications(query));
    }
}
