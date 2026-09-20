package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.response.NoShowRiskResponse;
import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.enums.AppointmentStatus;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.service.NoShowPredictionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NoShowPredictionServiceImpl implements NoShowPredictionService {

    private final AppointmentRepository appointmentRepository;

    public NoShowPredictionServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public NoShowRiskResponse assessNoShowRisk(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        int score = 15; // Baseline clinic risk percentage
        List<String> factors = new ArrayList<>();

        Long patientId = appointment.getPatient().getId();
        List<Appointment> patientHistory = appointmentRepository.findByPatientId(patientId);

        // 1. Previous No-Shows
        long priorNoShows = patientHistory.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.NO_SHOW && !a.getId().equals(appointmentId))
                .count();
        if (priorNoShows > 0) {
            int noShowPenalty = (int) Math.min(priorNoShows * 25, 50);
            score += noShowPenalty;
            factors.add("Patient has " + priorNoShows + " prior no-show recorded in history (+" + noShowPenalty + "% risk)");
        }

        // 2. Previous Cancellations
        long priorCancellations = patientHistory.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.CANCELLED && !a.getId().equals(appointmentId))
                .count();
        if (priorCancellations > 0) {
            int cancelPenalty = (int) Math.min(priorCancellations * 10, 20);
            score += cancelPenalty;
            factors.add("Patient has " + priorCancellations + " prior cancellations (+" + cancelPenalty + "% risk)");
        }

        // 3. Booking Lead Time
        if (appointment.getCreatedAt() != null && appointment.getAppointmentDate() != null) {
            long daysLead = ChronoUnit.DAYS.between(appointment.getCreatedAt().toLocalDate(), appointment.getAppointmentDate());
            if (daysLead > 14) {
                score += 15;
                factors.add("Extended lead time (" + daysLead + " days in advance, +15% risk)");
            } else if (daysLead <= 1) {
                score -= 10;
                factors.add("Short lead time (booked within 24h, -10% risk)");
            }
        }

        // 4. Time of Day (e.g. Early morning before 9:00 or late afternoon after 16:00 have higher variance)
        LocalTime aptTime = appointment.getAppointmentTime();
        if (aptTime != null) {
            if (aptTime.isBefore(LocalTime.of(9, 0))) {
                score += 10;
                factors.add("Early morning slot before 9:00 AM (+10% risk)");
            } else if (aptTime.isAfter(LocalTime.of(16, 0))) {
                score += 8;
                factors.add("Late afternoon slot after 4:00 PM (+8% risk)");
            }
        }

        // 5. Day of Week (e.g. Saturdays or Mondays)
        LocalDate aptDate = appointment.getAppointmentDate();
        if (aptDate != null) {
            DayOfWeek dow = aptDate.getDayOfWeek();
            if (dow == DayOfWeek.MONDAY) {
                score += 5;
                factors.add("Monday appointment (+5% risk)");
            } else if (dow == DayOfWeek.SATURDAY) {
                score += 5;
                factors.add("Weekend Saturday appointment (+5% risk)");
            }
        }

        // 6. Good Attendance Bonus
        long completedVisits = patientHistory.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.COMPLETED)
                .count();
        if (completedVisits >= 2 && priorNoShows == 0) {
            score -= 15;
            factors.add("Reliable patient history with " + completedVisits + " completed visits (-15% risk)");
        }

        // Clamp score between 5% and 95%
        score = Math.max(5, Math.min(95, score));

        String riskLevel;
        if (score >= 60) {
            riskLevel = "HIGH";
        } else if (score >= 35) {
            riskLevel = "MEDIUM";
        } else {
            riskLevel = "LOW";
        }

        if (factors.isEmpty()) {
            factors.add("Standard appointment parameters. No adverse historical factors.");
        }

        return new NoShowRiskResponse(
                appointment.getId(),
                appointment.getAppointmentNumber(),
                appointment.getPatient().getFullName(),
                score,
                riskLevel,
                factors
        );
    }
}
