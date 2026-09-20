package com.sunrise.dental.controller;

import com.sunrise.dental.dto.request.ToothRecordRequest;
import com.sunrise.dental.dto.response.DentalChartResponse;
import com.sunrise.dental.dto.response.ToothHistoryResponse;
import com.sunrise.dental.dto.response.ToothRecordResponse;
import com.sunrise.dental.service.DentalChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dental-chart")
@Tag(name = "Dental Tooth Chart", description = "Interactive FDI 32-tooth chart, condition status, and tooth history")
public class DentalChartController {

    private final DentalChartService dentalChartService;

    public DentalChartController(DentalChartService dentalChartService) {
        this.dentalChartService = dentalChartService;
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get complete FDI 32-tooth dental chart grouped into 4 quadrants for a patient")
    public ResponseEntity<DentalChartResponse> getPatientChart(@PathVariable Long patientId) {
        return ResponseEntity.ok(dentalChartService.getDentalChartByPatientId(patientId));
    }

    @PutMapping("/tooth")
    @Operation(summary = "Update tooth condition and record historical change audit")
    public ResponseEntity<ToothRecordResponse> updateToothCondition(
            @Valid @RequestBody ToothRecordRequest request) {
        return ResponseEntity.ok(dentalChartService.updateToothCondition(request));
    }

    @GetMapping("/tooth/{toothRecordId}/history")
    @Operation(summary = "Get condition transition history for a specific tooth")
    public ResponseEntity<List<ToothHistoryResponse>> getToothHistory(@PathVariable Long toothRecordId) {
        return ResponseEntity.ok(dentalChartService.getToothHistory(toothRecordId));
    }
}
