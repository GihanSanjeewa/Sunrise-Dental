package com.sunrise.dental.repository;

import com.sunrise.dental.entity.InventoryTransaction;
import com.sunrise.dental.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByInventoryItemIdOrderByTransactionDateDesc(Long inventoryItemId);

    List<InventoryTransaction> findByTransactionTypeOrderByTransactionDateDesc(TransactionType transactionType);
}
