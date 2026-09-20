package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.PurchaseOrderRequest;
import com.sunrise.dental.dto.response.PurchaseOrderResponse;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.enums.PurchaseOrderStatus;
import com.sunrise.dental.repository.*;
import com.sunrise.dental.service.impl.PurchaseOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private PurchaseOrderServiceImpl purchaseOrderService;

    private Supplier supplier;
    private InventoryItem item;

    @BeforeEach
    void setUp() {
        supplier = new Supplier("SUP-0001", "MedTech Lanka", "Kamal", "0114445566", "kamal@medtech.lk", "Colombo", "ACTIVE");
        supplier.setId(1L);

        item = new InventoryItem("ITEM-0001", "Dental Needles 27G", "ANESTHESIA", supplier,
                10, 5, "Box", BigDecimal.valueOf(1500.00), null);
        item.setId(1L);
    }

    @Test
    @DisplayName("Create purchase order with items and calculate total amount")
    void shouldCreatePurchaseOrderSuccessfully() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(purchaseOrderRepository.findMaxId()).thenReturn(0L);
        when(purchaseOrderRepository.existsByPoNumber(anyString())).thenReturn(false);

        PurchaseOrder savedPo = new PurchaseOrder("PO-2026-000001", supplier, LocalDate.now(),
                LocalDate.now().plusDays(7), PurchaseOrderStatus.ORDERED, BigDecimal.ZERO, "Urgent restocking");
        savedPo.setId(5L);

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(savedPo);
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));

        PurchaseOrderRequest.PurchaseOrderItemDto itemDto = new PurchaseOrderRequest.PurchaseOrderItemDto();
        itemDto.setInventoryItemId(1L);
        itemDto.setQuantityOrdered(20);
        itemDto.setUnitCost(BigDecimal.valueOf(1500.00));

        PurchaseOrderRequest poReq = new PurchaseOrderRequest();
        poReq.setSupplierId(1L);
        poReq.setExpectedDeliveryDate(LocalDate.now().plusDays(7));
        poReq.setNotes("Urgent restocking");
        poReq.setItems(List.of(itemDto));

        PurchaseOrderResponse response = purchaseOrderService.createPurchaseOrder(poReq, "Admin");

        assertNotNull(response);
        assertEquals("PO-2026-000001", response.getPoNumber());
        verify(purchaseOrderRepository, atLeastOnce()).save(any(PurchaseOrder.class));
        verify(purchaseOrderItemRepository, times(1)).save(any(PurchaseOrderItem.class));
    }

    @Test
    @DisplayName("Goods Receipt Automation: Setting PO status to RECEIVED automatically increments inventory stock")
    void shouldAutoIncrementStockWhenReceived() {
        PurchaseOrder po = new PurchaseOrder("PO-2026-000001", supplier, LocalDate.now(),
                LocalDate.now().plusDays(7), PurchaseOrderStatus.ORDERED, BigDecimal.valueOf(30000.00), "Restocking");
        po.setId(1L);

        PurchaseOrderItem poItem = new PurchaseOrderItem(po, item, 20, BigDecimal.valueOf(1500.00), BigDecimal.valueOf(30000.00));
        po.getItems().add(poItem);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(po);
        when(purchaseOrderItemRepository.findByPurchaseOrderId(1L)).thenReturn(List.of(poItem));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        PurchaseOrderResponse response = purchaseOrderService.updatePurchaseOrderStatus(1L, PurchaseOrderStatus.RECEIVED, "WarehouseStaff");

        assertNotNull(response);
        assertEquals(PurchaseOrderStatus.RECEIVED, response.getStatus());

        // Initial 10 + 20 from PO = 30
        assertEquals(30, item.getCurrentQuantity());
        verify(inventoryItemRepository, times(1)).save(item);
        verify(transactionRepository, times(1)).save(any(InventoryTransaction.class));
        verify(auditService, atLeastOnce()).logAction(anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
