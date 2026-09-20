package com.sunrise.dental.repository;

import com.sunrise.dental.entity.ClinicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClinicalRecordRepository extends JpaRepository<ClinicalRecord, Long> {

    Optional<ClinicalRecord> findByRecordNumber(String recordNumber);

    boolean existsByRecordNumber(String recordNumber);

    List<ClinicalRecord> findByPatientIdOrderByVisitDateDescCreatedAtDesc(Long patientId);

    List<ClinicalRecord> findByDentistIdOrderByVisitDateDesc(Long dentistId);

    List<ClinicalRecord> findByAppointmentId(Long appointmentId);

    List<ClinicalRecord> findByVisitDate(LocalDate visitDate);

    @Query("SELECT MAX(c.id) FROM ClinicalRecord c")
    Long findMaxId();
}
