package com.sunrise.dental.dto.response;

import com.sunrise.dental.enums.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class PurchaseOrderResponse {

    private Long id;
    private String poNumber;
    private Long supplierId;
    private String supplierCode;
    private String supplierName;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private String notes;
    private List<PurchaseOrderItemResponse> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PurchaseOrderResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getExpectedDeliveryDate() {
        return expectedDeliveryDate;
    }

    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
    }

    public PurchaseOrderStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseOrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PurchaseOrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItemResponse> items) {
        this.items = items;
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

    public static class PurchaseOrderItemResponse {
        private Long id;
        private Long inventoryItemId;
        private String itemCode;
        private String itemName;
        private Integer quantityOrdered;
        private BigDecimal unitCost;
        private BigDecimal totalCost;

        public PurchaseOrderItemResponse() {
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getInventoryItemId() {
            return inventoryItemId;
        }

        public void setInventoryItemId(Long inventoryItemId) {
            this.inventoryItemId = inventoryItemId;
        }

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public Integer getQuantityOrdered() {
            return quantityOrdered;
        }

        public void setQuantityOrdered(Integer quantityOrdered) {
            this.quantityOrdered = quantityOrdered;
        }

        public BigDecimal getUnitCost() {
            return unitCost;
        }

        public void setUnitCost(BigDecimal unitCost) {
            this.unitCost = unitCost;
        }

        public BigDecimal getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(BigDecimal totalCost) {
            this.totalCost = totalCost;
        }
    }
}
