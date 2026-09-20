package com.sunrise.dental.repository;

import com.sunrise.dental.entity.DentalToothRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DentalToothRecordRepository extends JpaRepository<DentalToothRecord, Long> {

    List<DentalToothRecord> findByPatientIdOrderByToothNumberAsc(Long patientId);

    Optional<DentalToothRecord> findByPatientIdAndToothNumber(Long patientId, Integer toothNumber);

    boolean existsByPatientIdAndToothNumber(Long patientId, Integer toothNumber);
}
