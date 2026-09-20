package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);

    boolean existsByPrescriptionNumber(String prescriptionNumber);

    List<Prescription> findByPatientIdOrderByPrescriptionDateDescCreatedAtDesc(Long patientId);

    List<Prescription> findByDentistIdOrderByPrescriptionDateDesc(Long dentistId);

    List<Prescription> findByClinicalRecordId(Long clinicalRecordId);

    @Query("SELECT MAX(p.id) FROM Prescription p")
    Long findMaxId();
}
