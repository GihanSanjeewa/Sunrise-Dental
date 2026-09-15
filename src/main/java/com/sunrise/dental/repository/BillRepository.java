package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Bill;
import com.sunrise.dental.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillNumber(String billNumber);

    Optional<Bill> findByAppointmentId(Long appointmentId);

    boolean existsByBillNumber(String billNumber);

    boolean existsByAppointmentId(Long appointmentId);

    List<Bill> findByPaymentStatus(PaymentStatus status);

    @Query("SELECT b FROM Bill b WHERE b.createdAt BETWEEN :start AND :end ORDER BY b.createdAt DESC")
    List<Bill> findBillsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.paymentStatus = 'PAID' AND b.createdAt BETWEEN :start AND :end")
    BigDecimal calculateRevenueBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Bill b WHERE b.paymentStatus = 'PENDING'")
    BigDecimal calculatePendingRevenue();

    @Query("SELECT MAX(b.id) FROM Bill b")
    Long findMaxId();
}
