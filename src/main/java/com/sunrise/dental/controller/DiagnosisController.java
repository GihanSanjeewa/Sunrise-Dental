package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.DiagnosisRequest;
import com.sunrise.dental.dto.response.DiagnosisResponse;
import com.sunrise.dental.service.DiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnoses")
@Tag(name = "Diagnosis Management", description = "Standardized diagnoses linked to clinical records, teeth, and severity")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PostMapping
    @Operation(summary = "Record new diagnosis for a patient / clinical examination")
    public ResponseEntity<DiagnosisResponse> createDiagnosis(@Valid @RequestBody DiagnosisRequest request) {
        DiagnosisResponse response = diagnosisService.createDiagnosis(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get diagnosis by ID")
    public ResponseEntity<DiagnosisResponse> getDiagnosisById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosisService.getDiagnosisById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get all diagnoses recorded for a patient")
    public ResponseEntity<List<DiagnosisResponse>> getDiagnosesByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(diagnosisService.getDiagnosesByPatientId(patientId));
    }

    @GetMapping("/clinical-record/{clinicalRecordId}")
    @Operation(summary = "Get diagnoses linked to a specific clinical record")
    public ResponseEntity<List<DiagnosisResponse>> getDiagnosesByClinicalRecord(@PathVariable Long clinicalRecordId) {
        return ResponseEntity.ok(diagnosisService.getDiagnosesByClinicalRecordId(clinicalRecordId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete diagnosis record")
    public ResponseEntity<Map<String, String>> deleteDiagnosis(@PathVariable Long id) {
        diagnosisService.deleteDiagnosis(id);
        return ResponseEntity.ok(Map.of("message", "Diagnosis deleted successfully."));
    }
}
