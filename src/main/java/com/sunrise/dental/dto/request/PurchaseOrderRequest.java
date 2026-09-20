package com.sunrise.dental.dto.request;

import com.sunrise.dental.enums.PurchaseOrderStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PurchaseOrderRequest {

    @NotNull(message = "Supplier ID is required")
    private Long supplierId;

    private LocalDate orderDate;

    private LocalDate expectedDeliveryDate;

    private PurchaseOrderStatus status;

    private String notes;

    @NotEmpty(message = "At least one purchase order item is required")
    private List<PurchaseOrderItemDto> items;

    public PurchaseOrderRequest() {
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<PurchaseOrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<PurchaseOrderItemDto> items) {
        this.items = items;
    }

    public static class PurchaseOrderItemDto {
        @NotNull(message = "Inventory item ID is required")
        private Long inventoryItemId;

        @NotNull(message = "Quantity ordered is required")
        private Integer quantityOrdered;

        private BigDecimal unitCost;

        public PurchaseOrderItemDto() {
        }

        public Long getInventoryItemId() {
            return inventoryItemId;
        }

        public void setInventoryItemId(Long inventoryItemId) {
            this.inventoryItemId = inventoryItemId;
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
    }
}
