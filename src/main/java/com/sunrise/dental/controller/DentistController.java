package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.DentistRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.dto.response.DentistResponse;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.service.DentistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dentists")
@Tag(name = "Dentist Management", description = "Dentist records, clinical specialties, roster schedules, and availability status")
public class DentistController {

    private final DentistService dentistService;

    public DentistController(DentistService dentistService) {
        this.dentistService = dentistService;
    }

    @PostMapping
    @Operation(summary = "Add a new dentist to clinic roster (Admin only)")
    public ResponseEntity<DentistResponse> addDentist(@Valid @RequestBody DentistRequest request) {
        DentistResponse response = dentistService.addDentist(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all dentists")
    public ResponseEntity<List<DentistResponse>> getAllDentists() {
        return ResponseEntity.ok(dentistService.getAllDentists());
    }

    @GetMapping("/available")
    @Operation(summary = "List dentists with AVAILABLE status for appointment booking")
    public ResponseEntity<List<DentistResponse>> getAvailableDentists() {
        return ResponseEntity.ok(dentistService.getAvailableDentists());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dentist details by database ID")
    public ResponseEntity<DentistResponse> getDentistById(@PathVariable Long id) {
        return ResponseEntity.ok(dentistService.getDentistById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update dentist details")
    public ResponseEntity<DentistResponse> updateDentist(@PathVariable Long id, @Valid @RequestBody DentistRequest request) {
        return ResponseEntity.ok(dentistService.updateDentist(id, request));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update dentist availability status (AVAILABLE, ON_LEAVE, INACTIVE)")
    public ResponseEntity<DentistResponse> updateStatus(@PathVariable Long id, @RequestParam DentistStatus status) {
        return ResponseEntity.ok(dentistService.updateAvailabilityStatus(id, status));
    }

    @GetMapping("/search")
    @Operation(summary = "Search dentists by name, specialization, or dentist number")
    public ResponseEntity<List<DentistResponse>> searchDentists(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(dentistService.searchDentists(query));
    }

    @GetMapping("/{id}/appointments")
    @Operation(summary = "View scheduled appointments roster for a specific dentist")
    public ResponseEntity<List<AppointmentResponse>> getDentistAppointments(@PathVariable Long id) {
        return ResponseEntity.ok(dentistService.getDentistAppointments(id));
    }
}
