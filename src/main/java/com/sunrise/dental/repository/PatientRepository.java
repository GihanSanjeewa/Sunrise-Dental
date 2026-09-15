package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPatientNumber(String patientNumber);
    boolean existsByPatientNumber(String patientNumber);
    boolean existsByContactNumber(String contactNumber);
    boolean existsByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE " +
           "LOWER(p.patientNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.contactNumber LIKE CONCAT('%', :query, '%')")
    List<Patient> searchPatients(@Param("query") String query);

    @Query("SELECT MAX(p.id) FROM Patient p")
    Long findMaxId();
}
