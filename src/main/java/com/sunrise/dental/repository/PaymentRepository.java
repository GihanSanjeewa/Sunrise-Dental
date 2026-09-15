package com.sunrise.dental.repository;

import com.sunrise.dental.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    List<Payment> findByBillId(Long billId);

    @Query("SELECT MAX(p.id) FROM Payment p")
    Long findMaxId();
}
