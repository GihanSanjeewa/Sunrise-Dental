package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.AppointmentRequest;
import com.sunrise.dental.dto.request.AppointmentStatusUpdateRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.enums.AppointmentStatus;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    AppointmentResponse getAppointmentById(Long id);
    AppointmentResponse getAppointmentByNumber(String appointmentNumber);
    AppointmentResponse updateAppointment(Long id, AppointmentRequest request);
    AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatusUpdateRequest request);
    AppointmentResponse cancelAppointment(Long id);
    List<AppointmentResponse> getAllAppointments();
    List<AppointmentResponse> searchAppointments(String appointmentNumber, String patientNumber,
                                                String patientName, String contactNumber,
                                                Long dentistId, LocalDate date, AppointmentStatus status);
    List<AppointmentResponse> getTodayAppointments();
}
