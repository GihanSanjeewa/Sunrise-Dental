package com.sunrise.dental.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_code", nullable = false, unique = true, length = 30)
    private String itemCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity = 0;

    @Column(name = "minimum_quantity", nullable = false)
    private Integer minimumQuantity = 10;

    @Column(nullable = false, length = 30)
    private String unit; // PIECES, BOXES, BOTTLES, TUBES, PACKS

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitCost = BigDecimal.ZERO;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(nullable = false, length = 30)
    private String status = "IN_STOCK"; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public InventoryItem() {
    }

    public InventoryItem(String itemCode, String name, String category, Supplier supplier,
                         Integer currentQuantity, Integer minimumQuantity, String unit,
                         BigDecimal unitCost, LocalDate expiryDate) {
        this.itemCode = itemCode;
        this.name = name;
        this.category = category;
        this.supplier = supplier;
        this.currentQuantity = currentQuantity != null ? currentQuantity : 0;
        this.minimumQuantity = minimumQuantity != null ? minimumQuantity : 10;
        this.unit = unit;
        this.unitCost = unitCost != null ? unitCost : BigDecimal.ZERO;
        this.expiryDate = expiryDate;
        updateStatus();
    }

    public void updateStatus() {
        if (this.currentQuantity == null || this.currentQuantity <= 0) {
            this.status = "OUT_OF_STOCK";
        } else if (this.currentQuantity <= (this.minimumQuantity != null ? this.minimumQuantity : 10)) {
            this.status = "LOW_STOCK";
        } else {
            this.status = "IN_STOCK";
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        updateStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Integer getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
        updateStatus();
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
        updateStatus();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
