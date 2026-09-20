package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long> {

    List<Medication> findByIsActiveTrueOrderByNameAsc();

    List<Medication> findByNameContainingIgnoreCaseOrGenericNameContainingIgnoreCase(String name, String genericName);
}
