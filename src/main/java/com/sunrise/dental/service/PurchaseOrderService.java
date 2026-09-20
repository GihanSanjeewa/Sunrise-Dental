package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.PurchaseOrderRequest;
import com.sunrise.dental.dto.response.PurchaseOrderResponse;
import com.sunrise.dental.enums.PurchaseOrderStatus;

import java.util.List;

public interface PurchaseOrderService {

    PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request, String performedBy);

    PurchaseOrderResponse getPurchaseOrderById(Long id);

    PurchaseOrderResponse getPurchaseOrderByNumber(String poNumber);

    PurchaseOrderResponse updatePurchaseOrderStatus(Long id, PurchaseOrderStatus status, String performedBy);

    List<PurchaseOrderResponse> getAllPurchaseOrders();

    List<PurchaseOrderResponse> getPurchaseOrdersBySupplierId(Long supplierId);

    List<PurchaseOrderResponse> getPurchaseOrdersByStatus(PurchaseOrderStatus status);

    void deletePurchaseOrder(Long id);
}
