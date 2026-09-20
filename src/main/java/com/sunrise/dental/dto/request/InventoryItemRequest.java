package com.sunrise.dental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InventoryItemRequest {

    @NotBlank(message = "Item name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    private Long supplierId;

    @NotNull(message = "Current quantity is required")
    private Integer currentQuantity;

    @NotNull(message = "Minimum quantity threshold is required")
    private Integer minimumQuantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    private BigDecimal unitCost;

    private LocalDate expiryDate;

    public InventoryItemRequest() {
    }

    public InventoryItemRequest(String name, String category, String unit, Integer currentQuantity,
                                Integer minimumQuantity, BigDecimal unitCost, Long supplierId, LocalDate expiryDate) {
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.currentQuantity = currentQuantity;
        this.minimumQuantity = minimumQuantity;
        this.unitCost = unitCost;
        this.supplierId = supplierId;
        this.expiryDate = expiryDate;
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

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
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
}
