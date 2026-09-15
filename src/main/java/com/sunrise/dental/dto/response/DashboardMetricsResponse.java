package com.sunrise.dental.dto.response;

import java.math.BigDecimal;

public class DashboardMetricsResponse {
    private long todayAppointments;
    private long pendingAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private long totalPatients;
    private long availableDentists;
    private BigDecimal todayRevenue;
    private BigDecimal pendingRevenue;

    public DashboardMetricsResponse() {
        this.todayRevenue = BigDecimal.ZERO;
        this.pendingRevenue = BigDecimal.ZERO;
    }

    public DashboardMetricsResponse(long todayAppointments, long pendingAppointments, long completedAppointments,
                                    long cancelledAppointments, long totalPatients, long availableDentists,
                                    BigDecimal todayRevenue, BigDecimal pendingRevenue) {
        this.todayAppointments = todayAppointments;
        this.pendingAppointments = pendingAppointments;
        this.completedAppointments = completedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.totalPatients = totalPatients;
        this.availableDentists = availableDentists;
        this.todayRevenue = todayRevenue != null ? todayRevenue : BigDecimal.ZERO;
        this.pendingRevenue = pendingRevenue != null ? pendingRevenue : BigDecimal.ZERO;
    }

    public long getTodayAppointments() {
        return todayAppointments;
    }

    public void setTodayAppointments(long todayAppointments) {
        this.todayAppointments = todayAppointments;
    }

    public long getPendingAppointments() {
        return pendingAppointments;
    }

    public void setPendingAppointments(long pendingAppointments) {
        this.pendingAppointments = pendingAppointments;
    }

    public long getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(long completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public long getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(long cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public long getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(long totalPatients) {
        this.totalPatients = totalPatients;
    }

    public long getAvailableDentists() {
        return availableDentists;
    }

    public void setAvailableDentists(long availableDentists) {
        this.availableDentists = availableDentists;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public BigDecimal getPendingRevenue() {
        return pendingRevenue;
    }

    public void setPendingRevenue(BigDecimal pendingRevenue) {
        this.pendingRevenue = pendingRevenue;
    }
}
