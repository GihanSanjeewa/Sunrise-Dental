package com.sunrise.dental.service;

import com.sunrise.dental.dto.response.DashboardMetricsResponse;
import com.sunrise.dental.dto.response.ReportSummaryResponse;
import com.sunrise.dental.enums.AppointmentStatus;

import java.time.LocalDate;

public interface ReportService {
    DashboardMetricsResponse getDashboardMetrics();
    ReportSummaryResponse getDailyAppointmentReport(LocalDate date);
    ReportSummaryResponse getMonthlyAppointmentReport(int year, int month);
    ReportSummaryResponse getDentistAppointmentReport(Long dentistId, LocalDate fromDate, LocalDate toDate);
    ReportSummaryResponse getRevenueReport(LocalDate fromDate, LocalDate toDate);
    ReportSummaryResponse getPendingPaymentReport();
    ReportSummaryResponse getCancelledAppointmentReport(LocalDate fromDate, LocalDate toDate);
}
