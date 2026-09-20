package com.sunrise.dental.repository;

import com.sunrise.dental.entity.TreatmentPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentPlanItemRepository extends JpaRepository<TreatmentPlanItem, Long> {

    List<TreatmentPlanItem> findByTreatmentPlanId(Long treatmentPlanId);

    List<TreatmentPlanItem> findByTreatmentId(Long treatmentId);
}
