package com.sunrise.dental.repository;

import com.sunrise.dental.entity.TreatmentPlan;
import com.sunrise.dental.enums.TreatmentPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, Long> {

    Optional<TreatmentPlan> findByPlanNumber(String planNumber);

    boolean existsByPlanNumber(String planNumber);

    List<TreatmentPlan> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<TreatmentPlan> findByDentistIdOrderByCreatedAtDesc(Long dentistId);

    List<TreatmentPlan> findByStatus(TreatmentPlanStatus status);

    @Query("SELECT MAX(t.id) FROM TreatmentPlan t")
    Long findMaxId();

    long countByStatus(TreatmentPlanStatus status);
}
