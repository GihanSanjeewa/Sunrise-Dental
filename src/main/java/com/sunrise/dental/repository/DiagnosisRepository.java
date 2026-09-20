package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    List<Diagnosis> findByPatientIdOrderByDiagnosedDateDescCreatedAtDesc(Long patientId);

    List<Diagnosis> findByClinicalRecordId(Long clinicalRecordId);

    List<Diagnosis> findByDentistId(Long dentistId);
}
