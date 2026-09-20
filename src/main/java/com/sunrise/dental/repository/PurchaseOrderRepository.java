package com.sunrise.dental.repository;

import com.sunrise.dental.entity.PurchaseOrder;
import com.sunrise.dental.enums.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    boolean existsByPoNumber(String poNumber);

    List<PurchaseOrder> findBySupplierIdOrderByOrderDateDesc(Long supplierId);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);

    @Query("SELECT MAX(p.id) FROM PurchaseOrder p")
    Long findMaxId();
}
