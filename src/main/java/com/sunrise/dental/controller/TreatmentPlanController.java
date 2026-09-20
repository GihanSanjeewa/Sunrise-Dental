package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.TreatmentPlanItemRequest;
import com.sunrise.dental.dto.request.TreatmentPlanRequest;
import com.sunrise.dental.dto.response.TreatmentPlanItemResponse;
import com.sunrise.dental.dto.response.TreatmentPlanResponse;
import com.sunrise.dental.enums.TreatmentItemStatus;
import com.sunrise.dental.enums.TreatmentPlanStatus;
import com.sunrise.dental.service.TreatmentPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/treatment-plans")
@Tag(name = "Treatment Planning", description = "Multi-stage treatment plans, dynamic catalog pricing, and status tracking")
public class TreatmentPlanController {

    private final TreatmentPlanService treatmentPlanService;

    public TreatmentPlanController(TreatmentPlanService treatmentPlanService) {
        this.treatmentPlanService = treatmentPlanService;
    }

    @PostMapping
    @Operation(summary = "Create structured treatment plan with dynamic catalog pricing")
    public ResponseEntity<TreatmentPlanResponse> createPlan(@Valid @RequestBody TreatmentPlanRequest request) {
        TreatmentPlanResponse response = treatmentPlanService.createTreatmentPlan(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get treatment plan with items and calculated totals by ID")
    public ResponseEntity<TreatmentPlanResponse> getPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(treatmentPlanService.getTreatmentPlanById(id));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get all treatment plans for a patient")
    public ResponseEntity<List<TreatmentPlanResponse>> getPlansByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(treatmentPlanService.getTreatmentPlansByPatientId(patientId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update treatment plan overall status (DRAFT, PROPOSED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED)")
    public ResponseEntity<TreatmentPlanResponse> updatePlanStatus(
            @PathVariable Long id,
            @RequestParam TreatmentPlanStatus status) {
        return ResponseEntity.ok(treatmentPlanService.updateTreatmentPlanStatus(id, status));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add item to treatment plan")
    public ResponseEntity<TreatmentPlanItemResponse> addItem(
            @PathVariable Long id,
            @Valid @RequestBody TreatmentPlanItemRequest request) {
        return new ResponseEntity<>(treatmentPlanService.addItemToPlan(id, request), HttpStatus.CREATED);
    }

    @PatchMapping("/items/{itemId}/status")
    @Operation(summary = "Update individual treatment plan item status (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)")
    public ResponseEntity<TreatmentPlanItemResponse> updateItemStatus(
            @PathVariable Long itemId,
            @RequestParam TreatmentItemStatus status) {
        return ResponseEntity.ok(treatmentPlanService.updateItemStatus(itemId, status));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Remove item from treatment plan")
    public ResponseEntity<Map<String, String>> removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        treatmentPlanService.removeItemFromPlan(id, itemId);
        return ResponseEntity.ok(Map.of("message", "Item removed successfully."));
    }
}
