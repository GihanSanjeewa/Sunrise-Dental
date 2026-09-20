package com.sunrise.dental.repository;

import com.sunrise.dental.entity.TreatmentSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TreatmentSessionRepository extends JpaRepository<TreatmentSession, Long> {

    List<TreatmentSession> findByTreatmentPlanIdOrderBySessionNumberAsc(Long treatmentPlanId);

    List<TreatmentSession> findByPatientIdOrderBySessionDateDesc(Long patientId);

    List<TreatmentSession> findByDentistIdAndSessionDate(Long dentistId, LocalDate sessionDate);

    List<TreatmentSession> findByAppointmentId(Long appointmentId);
}
