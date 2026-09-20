package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.InventoryItemRequest;
import com.sunrise.dental.dto.request.StockAdjustmentRequest;
import com.sunrise.dental.dto.response.InventoryItemResponse;
import com.sunrise.dental.entity.InventoryItem;
import com.sunrise.dental.entity.InventoryTransaction;
import com.sunrise.dental.entity.Supplier;
import com.sunrise.dental.enums.TransactionType;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.InventoryItemRepository;
import com.sunrise.dental.repository.InventoryTransactionRepository;
import com.sunrise.dental.repository.SupplierRepository;
import com.sunrise.dental.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private InventoryItem item;
    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new Supplier("SUP-0001", "Dental Supplies Lanka", "Sunil", "0112345678", "supplies@lanka.lk", "Colombo", "ACTIVE");
        supplier.setId(1L);

        item = new InventoryItem("ITEM-0001", "Composite Resin A2", "RESTORATIVE", supplier,
                20, 5, "Syringe", BigDecimal.valueOf(4500.00), null);
        item.setId(1L);
    }

    @Test
    @DisplayName("Create inventory item with auto-generated item code")
    void shouldCreateInventoryItemSuccessfully() {
        when(inventoryItemRepository.findMaxId()).thenReturn(0L);
        when(inventoryItemRepository.existsByItemCode(anyString())).thenReturn(false);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        InventoryItemRequest req = new InventoryItemRequest(
                "Composite Resin A2", "RESTORATIVE", "Syringe", 20, 5,
                BigDecimal.valueOf(4500.00), null, null
        );

        InventoryItemResponse response = inventoryService.createItem(req);

        assertNotNull(response);
        assertEquals("ITEM-0001", response.getItemCode());
        assertEquals(20, response.getCurrentQuantity());
        verify(inventoryItemRepository, times(1)).save(any(InventoryItem.class));
        verify(auditService, times(1)).logAction(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Adjust stock for USAGE decreases item quantity and logs transaction")
    void shouldDeductStockForUsage() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        StockAdjustmentRequest adjReq = new StockAdjustmentRequest();
        adjReq.setInventoryItemId(1L);
        adjReq.setTransactionType(TransactionType.USAGE);
        adjReq.setQuantity(3);
        adjReq.setReason("Used during composite restorations");

        InventoryItemResponse response = inventoryService.adjustStock(adjReq, "Dr. Samantha");

        assertNotNull(response);
        // Initial 20 - 3 = 17
        assertEquals(17, item.getCurrentQuantity());
        verify(transactionRepository, times(1)).save(any(InventoryTransaction.class));
        verify(auditService, times(1)).logAction(anyString(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Reject stock adjustment if usage exceeds current available quantity")
    void shouldRejectUsageExceedingStock() {
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));

        StockAdjustmentRequest adjReq = new StockAdjustmentRequest();
        adjReq.setInventoryItemId(1L);
        adjReq.setTransactionType(TransactionType.USAGE);
        // Exceeds available 20
        adjReq.setQuantity(25);

        assertThrows(ValidationException.class, () -> {
            inventoryService.adjustStock(adjReq, "Dr. Samantha");
        });

        verify(transactionRepository, never()).save(any(InventoryTransaction.class));
    }

    @Test
    @DisplayName("Return low stock items when quantity is at or below reorder level")
    void shouldFindLowStockItems() {
        InventoryItem lowStockItem = new InventoryItem("ITEM-0002", "Latex Gloves Medium", "DISPOSABLE", supplier,
                3, 5, "Box", BigDecimal.valueOf(1200.00), null);
        lowStockItem.setId(2L);

        when(inventoryItemRepository.findLowStockItems()).thenReturn(List.of(lowStockItem));

        List<InventoryItemResponse> lowStock = inventoryService.getLowStockItems();

        assertNotNull(lowStock);
        assertEquals(1, lowStock.size());
        assertEquals("ITEM-0002", lowStock.get(0).getItemCode());
    }
}
