package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.PrescriptionRequest;
import com.sunrise.dental.dto.response.PrescriptionResponse;
import com.sunrise.dental.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
@Tag(name = "Prescription Management", description = "Prescriptions, dosage instructions, and printable Rx")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping
    @Operation(summary = "Create medication prescription for patient")
    public ResponseEntity<PrescriptionResponse> createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        PrescriptionResponse response = prescriptionService.createPrescription(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription details and items for printable Rx")
    public ResponseEntity<PrescriptionResponse> getPrescriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get all prescriptions issued to a patient")
    public ResponseEntity<List<PrescriptionResponse>> getPrescriptionsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByPatientId(patientId));
    }
}
