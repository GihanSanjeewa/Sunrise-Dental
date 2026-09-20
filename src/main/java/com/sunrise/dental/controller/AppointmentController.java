package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.AppointmentRequest;
import com.sunrise.dental.dto.request.AppointmentStatusUpdateRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.enums.AppointmentStatus;
import com.sunrise.dental.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import com.sunrise.dental.dto.response.NoShowRiskResponse;
import com.sunrise.dental.service.NoShowPredictionService;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointment Management", description = "Appointment booking, atomic double-booking conflict prevention, search, and lifecycle status tracking")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final NoShowPredictionService noShowPredictionService;

    public AppointmentController(AppointmentService appointmentService, NoShowPredictionService noShowPredictionService) {
        this.appointmentService = appointmentService;
        this.noShowPredictionService = noShowPredictionService;
    }

    @PostMapping
    @Operation(summary = "Schedule new appointment with strict double-booking conflict validation")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all scheduled appointments")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment details by database ID")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @GetMapping("/number/{appointmentNumber}")
    @Operation(summary = "Search and display appointment by unique appointment number (e.g. APT-2026-000001)")
    public ResponseEntity<AppointmentResponse> getAppointmentByNumber(@PathVariable String appointmentNumber) {
        return ResponseEntity.ok(appointmentService.getAppointmentByNumber(appointmentNumber));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment details and reschedule slot")
    public ResponseEntity<AppointmentResponse> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status (CONFIRMED, COMPLETED, NO_SHOW) and record clinical notes")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(@PathVariable Long id, @Valid @RequestBody AppointmentStatusUpdateRequest request) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel appointment and release the dentist's time slot for new bookings")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Multi-criteria appointment search (by appointment number, patient number, patient name, contact, dentist, date, status)")
    public ResponseEntity<List<AppointmentResponse>> searchAppointments(
            @RequestParam(required = false) String appointmentNumber,
            @RequestParam(required = false) String patientNumber,
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String contactNumber,
            @RequestParam(required = false) Long dentistId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.searchAppointments(appointmentNumber, patientNumber, patientName, contactNumber, dentistId, date, status));
    }

    @GetMapping("/today")
    @Operation(summary = "List today's scheduled appointments for active clinical roster")
    public ResponseEntity<List<AppointmentResponse>> getTodayAppointments() {
        return ResponseEntity.ok(appointmentService.getTodayAppointments());
    }

    @GetMapping("/queue/today")
    @Operation(summary = "Get daily appointment queue with token numbers and active waiting/treatment status")
    public ResponseEntity<List<AppointmentResponse>> getDailyQueue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getDailyQueue(date != null ? date : LocalDate.now()));
    }

    @PostMapping("/{id}/queue/call")
    @Operation(summary = "Call patient from waiting area (sets status to CALLED)")
    public ResponseEntity<AppointmentResponse> callPatient(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.callPatient(id));
    }

    @PostMapping("/{id}/queue/start")
    @Operation(summary = "Start dental treatment session in chair (sets status to IN_TREATMENT)")
    public ResponseEntity<AppointmentResponse> startTreatment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.startTreatment(id));
    }

    @PostMapping("/{id}/queue/complete")
    @Operation(summary = "Complete clinical visit (sets status to COMPLETED)")
    public ResponseEntity<AppointmentResponse> completeVisit(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.completeVisit(id));
    }

    @PostMapping("/{id}/queue/no-show")
    @Operation(summary = "Mark patient as no-show (sets status to NO_SHOW)")
    public ResponseEntity<AppointmentResponse> markNoShow(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.markNoShow(id));
    }

    @GetMapping("/{id}/no-show-risk")
    @Operation(summary = "Calculate AI/ML-ready no-show risk probability and contributing factors (decision-support estimate)")
    public ResponseEntity<NoShowRiskResponse> getNoShowRisk(@PathVariable Long id) {
        return ResponseEntity.ok(noShowPredictionService.assessNoShowRisk(id));
    }
}
