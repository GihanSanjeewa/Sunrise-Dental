package com.sunrise.dental.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentRequest {

    @NotNull(message = "Patient ID cannot be empty.")
    private Long patientId;

    @NotNull(message = "Dentist ID cannot be empty.")
    private Long dentistId;

    @NotNull(message = "Treatment ID cannot be empty.")
    private Long treatmentId;

    @NotNull(message = "Appointment date cannot be empty.")
    @FutureOrPresent(message = "Appointment date must be today or in the future.")
    private LocalDate appointmentDate;

    @NotNull(message = "Appointment time cannot be empty.")
    private LocalTime appointmentTime;

    private String notes;

    public AppointmentRequest() {
    }

    public AppointmentRequest(Long patientId, Long dentistId, Long treatmentId, LocalDate appointmentDate, LocalTime appointmentTime, String notes) {
        this.patientId = patientId;
        this.dentistId = dentistId;
        this.treatmentId = treatmentId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.notes = notes;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDentistId() {
        return dentistId;
    }

    public void setDentistId(Long dentistId) {
        this.dentistId = dentistId;
    }

    public Long getTreatmentId() {
        return treatmentId;
    }

    public void setTreatmentId(Long treatmentId) {
        this.treatmentId = treatmentId;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
