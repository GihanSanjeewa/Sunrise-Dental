package com.sunrise.dental.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class ReportSummaryResponse {
    private String reportTitle;
    private String period;
    private long totalCount;
    private BigDecimal totalAmount;
    private List<AppointmentResponse> appointments;
    private List<BillResponse> bills;

    public ReportSummaryResponse() {
        this.totalAmount = BigDecimal.ZERO;
    }

    public ReportSummaryResponse(String reportTitle, String period, long totalCount, BigDecimal totalAmount) {
        this.reportTitle = reportTitle;
        this.period = period;
        this.totalCount = totalCount;
        this.totalAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<AppointmentResponse> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentResponse> appointments) {
        this.appointments = appointments;
    }

    public List<BillResponse> getBills() {
        return bills;
    }

    public void setBills(List<BillResponse> bills) {
        this.bills = bills;
    }
}
