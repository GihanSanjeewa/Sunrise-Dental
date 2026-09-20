package com.sunrise.dental.repository;

import com.sunrise.dental.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByItemCode(String itemCode);

    boolean existsByItemCode(String itemCode);

    List<InventoryItem> findByCategory(String category);

    List<InventoryItem> findByStatus(String status);

    @Query("SELECT i FROM InventoryItem i WHERE i.currentQuantity <= i.minimumQuantity")
    List<InventoryItem> findLowStockItems();

    @Query("SELECT MAX(i.id) FROM InventoryItem i")
    Long findMaxId();

    long countByStatus(String status);
}
