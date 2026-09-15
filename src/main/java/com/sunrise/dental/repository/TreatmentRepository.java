package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    Optional<Treatment> findByTreatmentCode(String treatmentCode);
    boolean existsByTreatmentCode(String treatmentCode);
    List<Treatment> findByStatus(String status);

    @Query("SELECT MAX(t.id) FROM Treatment t")
    Long findMaxId();
}
