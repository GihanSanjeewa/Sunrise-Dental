package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.TreatmentSessionRequest;
import com.sunrise.dental.dto.response.TreatmentSessionResponse;
import com.sunrise.dental.enums.SessionStatus;
import com.sunrise.dental.service.TreatmentSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/treatment-sessions")
@Tag(name = "Treatment Sessions", description = "Session execution, clinical notes, and session completion")
public class TreatmentSessionController {

    private final TreatmentSessionService treatmentSessionService;

    public TreatmentSessionController(TreatmentSessionService treatmentSessionService) {
        this.treatmentSessionService = treatmentSessionService;
    }

    @PostMapping
    @Operation(summary = "Schedule or log a treatment session linked to a treatment plan")
    public ResponseEntity<TreatmentSessionResponse> createSession(@Valid @RequestBody TreatmentSessionRequest request) {
        TreatmentSessionResponse response = treatmentSessionService.createSession(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get treatment session details by ID")
    public ResponseEntity<TreatmentSessionResponse> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentSessionService.getSessionById(id));
    }

    @GetMapping("/treatment-plan/{planId}")
    @Operation(summary = "Get all treatment sessions linked to a specific treatment plan")
    public ResponseEntity<List<TreatmentSessionResponse>> getSessionsByPlanId(@PathVariable Long planId) {
        return ResponseEntity.ok(treatmentSessionService.getSessionsByTreatmentPlanId(planId));
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark treatment session as completed with final clinical notes")
    public ResponseEntity<TreatmentSessionResponse> completeSession(
            @PathVariable Long id,
            @RequestParam(required = false) String clinicalNotes) {
        return ResponseEntity.ok(treatmentSessionService.updateSessionStatus(id, SessionStatus.COMPLETED, clinicalNotes));
    }
}
