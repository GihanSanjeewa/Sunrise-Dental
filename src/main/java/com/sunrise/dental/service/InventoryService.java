package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.InventoryItemRequest;
import com.sunrise.dental.dto.request.StockAdjustmentRequest;
import com.sunrise.dental.dto.response.InventoryItemResponse;
import com.sunrise.dental.dto.response.InventoryTransactionResponse;

import java.util.List;

public interface InventoryService {

    InventoryItemResponse createItem(InventoryItemRequest request);

    InventoryItemResponse getItemById(Long id);

    InventoryItemResponse updateItem(Long id, InventoryItemRequest request);

    InventoryItemResponse adjustStock(StockAdjustmentRequest request, String performedBy);

    List<InventoryItemResponse> getAllItems();

    List<InventoryItemResponse> getLowStockItems();

    List<InventoryItemResponse> getItemsByCategory(String category);

    List<InventoryTransactionResponse> getTransactionsByItemId(Long itemId);

    void deleteItem(Long id);
}
