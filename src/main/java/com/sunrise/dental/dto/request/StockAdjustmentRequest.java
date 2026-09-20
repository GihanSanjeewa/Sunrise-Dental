package com.sunrise.dental.dto.request;

import com.sunrise.dental.enums.TransactionType;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentRequest {

    @NotNull(message = "Inventory item ID is required")
    private Long inventoryItemId;

    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType; // PURCHASE, USAGE, ADJUSTMENT, RETURN, EXPIRED

    @NotNull(message = "Quantity is required")
    private Integer quantity; // Positive for increase, or absolute number depending on transaction type

    private String reason;

    public StockAdjustmentRequest() {
    }

    public Long getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(Long inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
