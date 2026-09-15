package com.sunrise.dental.controller;

import com.sunrise.dental.dto.response.DashboardMetricsResponse;
import com.sunrise.dental.dto.response.ReportSummaryResponse;
import com.sunrise.dental.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Analytics & Reports", description = "Dashboard analytics, daily/monthly appointment reports, revenue analysis, and audit tracking")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get high-level aggregated KPI metrics for clinic executive dashboard")
    public ResponseEntity<DashboardMetricsResponse> getDashboardMetrics() {
        return ResponseEntity.ok(reportService.getDashboardMetrics());
    }

    @GetMapping("/daily-appointments")
    @Operation(summary = "Daily appointment schedule report with date filter")
    public ResponseEntity<ReportSummaryResponse> getDailyAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailyAppointmentReport(date));
    }

    @GetMapping("/monthly-appointments")
    @Operation(summary = "Monthly appointment trend report")
    public ResponseEntity<ReportSummaryResponse> getMonthlyAppointments(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(reportService.getMonthlyAppointmentReport(year, month));
    }

    @GetMapping("/dentist-appointments")
    @Operation(summary = "Dentist roster and appointment utilization report")
    public ResponseEntity<ReportSummaryResponse> getDentistAppointments(
            @RequestParam Long dentistId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(reportService.getDentistAppointmentReport(dentistId, fromDate, toDate));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Financial revenue report between specified date ranges")
    public ResponseEntity<ReportSummaryResponse> getRevenueReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(reportService.getRevenueReport(fromDate, toDate));
    }

    @GetMapping("/pending-payments")
    @Operation(summary = "Outstanding receivables and pending payments report")
    public ResponseEntity<ReportSummaryResponse> getPendingPayments() {
        return ResponseEntity.ok(reportService.getPendingPaymentReport());
    }

    @GetMapping("/cancelled-appointments")
    @Operation(summary = "Cancelled appointments audit report")
    public ResponseEntity<ReportSummaryResponse> getCancelledAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return ResponseEntity.ok(reportService.getCancelledAppointmentReport(fromDate, toDate));
    }
}
