package com.sunrise.dental.repository;

import com.sunrise.dental.entity.ToothHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToothHistoryRepository extends JpaRepository<ToothHistory, Long> {

    List<ToothHistory> findByDentalToothIdOrderByProcedureDateDescCreatedAtDesc(Long dentalToothId);

    List<ToothHistory> findByClinicalRecordId(Long clinicalRecordId);
}
