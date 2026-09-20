package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.ClinicalRecordRequest;
import com.sunrise.dental.dto.response.ClinicalRecordResponse;
import com.sunrise.dental.service.ClinicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinical-records")
@Tag(name = "Clinical Records", description = "Chronological clinical history, dental examinations, complaints, and observations")
public class ClinicalRecordController {

    private final ClinicalRecordService clinicalRecordService;

    public ClinicalRecordController(ClinicalRecordService clinicalRecordService) {
        this.clinicalRecordService = clinicalRecordService;
    }

    @PostMapping
    @Operation(summary = "Create new chronological clinical examination record")
    public ResponseEntity<ClinicalRecordResponse> createRecord(@Valid @RequestBody ClinicalRecordRequest request) {
        ClinicalRecordResponse response = clinicalRecordService.createClinicalRecord(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get clinical record by ID")
    public ResponseEntity<ClinicalRecordResponse> getRecordById(@PathVariable Long id) {
        return ResponseEntity.ok(clinicalRecordService.getClinicalRecordById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get chronological clinical examination history for a patient")
    public ResponseEntity<List<ClinicalRecordResponse>> getRecordsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(clinicalRecordService.getClinicalRecordsByPatientId(patientId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update clinical record observations and notes")
    public ResponseEntity<ClinicalRecordResponse> updateRecord(@PathVariable Long id, @Valid @RequestBody ClinicalRecordRequest request) {
        return ResponseEntity.ok(clinicalRecordService.updateClinicalRecord(id, request));
    }
}
