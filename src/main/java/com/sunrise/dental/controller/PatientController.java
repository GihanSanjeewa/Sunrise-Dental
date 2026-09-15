package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.PatientRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.dto.response.PatientResponse;
import com.sunrise.dental.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patient Management", description = "Patient registration, searching, profile updating, and treatment history")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @Operation(summary = "Register a new patient with unique sequence number")
    public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all registered patients")
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient profile by database ID")
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/number/{patientNumber}")
    @Operation(summary = "Get patient profile by unique patient number (e.g. P-000001)")
    public ResponseEntity<PatientResponse> getPatientByNumber(@PathVariable String patientNumber) {
        return ResponseEntity.ok(patientService.getPatientByNumber(patientNumber));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient demographic details")
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    @GetMapping("/search")
    @Operation(summary = "Search patients by patient number, full name, or phone number")
    public ResponseEntity<List<PatientResponse>> searchPatients(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(patientService.searchPatients(query));
    }

    @GetMapping("/{id}/appointments")
    @Operation(summary = "View patient appointment and treatment history")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointmentHistory(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientAppointmentHistory(id));
    }
}
