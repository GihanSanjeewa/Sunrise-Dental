package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.PurchaseOrderRequest;
import com.sunrise.dental.dto.response.PurchaseOrderResponse;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.enums.PurchaseOrderStatus;
import com.sunrise.dental.enums.TransactionType;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.*;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.PurchaseOrderService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AuditService auditService;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository,
                                   PurchaseOrderItemRepository purchaseOrderItemRepository,
                                   SupplierRepository supplierRepository,
                                   InventoryItemRepository inventoryItemRepository,
                                   InventoryTransactionRepository inventoryTransactionRepository,
                                   AuditService auditService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.auditService = auditService;
    }

    @Override
    public PurchaseOrderResponse createPurchaseOrder(PurchaseOrderRequest request, String performedBy) {
        if (request.getSupplierId() == null) {
            throw new ValidationException("Supplier ID is required.");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ValidationException("At least one purchase order item is required.");
        }

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + request.getSupplierId()));

        Long maxId = purchaseOrderRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String poNumber = IdSequenceGenerator.generatePurchaseOrderNumber(nextId);

        while (purchaseOrderRepository.existsByPoNumber(poNumber)) {
            nextId++;
            poNumber = IdSequenceGenerator.generatePurchaseOrderNumber(nextId);
        }

        LocalDate orderDate = request.getOrderDate() != null ? request.getOrderDate() : LocalDate.now();

        PurchaseOrder po = new PurchaseOrder(
                poNumber,
                supplier,
                orderDate,
                request.getExpectedDeliveryDate(),
                request.getStatus() != null ? request.getStatus() : PurchaseOrderStatus.ORDERED,
                BigDecimal.ZERO,
                request.getNotes()
        );

        PurchaseOrder savedPo = purchaseOrderRepository.save(po);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<PurchaseOrderItem> items = new ArrayList<>();

        for (PurchaseOrderRequest.PurchaseOrderItemDto itemDto : request.getItems()) {
            InventoryItem invItem = inventoryItemRepository.findById(itemDto.getInventoryItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + itemDto.getInventoryItemId()));

            BigDecimal unitCost = itemDto.getUnitCost() != null ? itemDto.getUnitCost() : invItem.getUnitCost();
            BigDecimal totalCost = unitCost.multiply(BigDecimal.valueOf(itemDto.getQuantityOrdered()));

            PurchaseOrderItem item = new PurchaseOrderItem(
                    savedPo,
                    invItem,
                    itemDto.getQuantityOrdered(),
                    unitCost,
                    totalCost
            );

            items.add(purchaseOrderItemRepository.save(item));
            totalAmount = totalAmount.add(totalCost);
        }

        savedPo.setTotalAmount(totalAmount);
        savedPo.setItems(items);
        purchaseOrderRepository.save(savedPo);

        auditService.logAction(performedBy != null ? performedBy : "ADMIN", "CREATE", "PURCHASE_ORDER", savedPo.getPoNumber(),
                "Created purchase order " + savedPo.getPoNumber() + " with " + items.size() + " items. Total: Rs. " + totalAmount);

        return mapToResponse(savedPo);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderById(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + id));
        return mapToResponse(po);
    }

    @Override
    @Transactional(readOnly = true)
    public PurchaseOrderResponse getPurchaseOrderByNumber(String poNumber) {
        PurchaseOrder po = purchaseOrderRepository.findByPoNumber(poNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with number: " + poNumber));
        return mapToResponse(po);
    }

    @Override
    public PurchaseOrderResponse updatePurchaseOrderStatus(Long id, PurchaseOrderStatus newStatus, String performedBy) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + id));

        PurchaseOrderStatus oldStatus = po.getStatus();
        if (oldStatus == newStatus) {
            return mapToResponse(po);
        }

        po.setStatus(newStatus);
        PurchaseOrder updated = purchaseOrderRepository.save(po);

        // Core Requirement: Supplier -> Purchase Order -> Stock Increase
        // When received, automatically increment stock for all ordered items and create PURCHASE transactions
        if (newStatus == PurchaseOrderStatus.RECEIVED && oldStatus != PurchaseOrderStatus.RECEIVED) {
            List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(po.getId());
            for (PurchaseOrderItem it : items) {
                InventoryItem invItem = it.getInventoryItem();
                int oldStock = invItem.getCurrentQuantity();
                int newStock = oldStock + it.getQuantityOrdered();
                invItem.setCurrentQuantity(newStock);
                inventoryItemRepository.save(invItem);

                InventoryTransaction tx = new InventoryTransaction(
                        invItem,
                        TransactionType.PURCHASE,
                        it.getQuantityOrdered(),
                        performedBy != null ? performedBy : "STAFF",
                        "Goods received from Purchase Order " + po.getPoNumber()
                );
                inventoryTransactionRepository.save(tx);
            }
            auditService.logAction(performedBy != null ? performedBy : "ADMIN", "GOODS_RECEIVED", "PURCHASE_ORDER", po.getPoNumber(),
                    "Goods received! Stock increased for " + items.size() + " items under PO " + po.getPoNumber());
        } else {
            auditService.logAction(performedBy != null ? performedBy : "ADMIN", "UPDATE_STATUS", "PURCHASE_ORDER", po.getPoNumber(),
                    "Changed PO status from " + oldStatus + " to " + newStatus);
        }

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getPurchaseOrdersBySupplierId(Long supplierId) {
        return purchaseOrderRepository.findBySupplierIdOrderByOrderDateDesc(supplierId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponse> getPurchaseOrdersByStatus(PurchaseOrderStatus status) {
        return purchaseOrderRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with ID: " + id));
        purchaseOrderRepository.delete(po);
        auditService.logAction("ADMIN", "DELETE", "PURCHASE_ORDER", po.getPoNumber(),
                "Deleted purchase order " + po.getPoNumber());
    }

    private PurchaseOrderResponse mapToResponse(PurchaseOrder p) {
        PurchaseOrderResponse resp = new PurchaseOrderResponse();
        resp.setId(p.getId());
        resp.setPoNumber(p.getPoNumber());

        if (p.getSupplier() != null) {
            resp.setSupplierId(p.getSupplier().getId());
            resp.setSupplierCode(p.getSupplier().getSupplierCode());
            resp.setSupplierName(p.getSupplier().getName());
        }

        resp.setOrderDate(p.getOrderDate());
        resp.setExpectedDeliveryDate(p.getExpectedDeliveryDate());
        resp.setStatus(p.getStatus());
        resp.setTotalAmount(p.getTotalAmount());
        resp.setNotes(p.getNotes());
        resp.setCreatedAt(p.getCreatedAt());
        resp.setUpdatedAt(p.getUpdatedAt());

        List<PurchaseOrderItem> items = purchaseOrderItemRepository.findByPurchaseOrderId(p.getId());
        resp.setItems(items.stream().map(it -> {
            PurchaseOrderResponse.PurchaseOrderItemResponse itemResp = new PurchaseOrderResponse.PurchaseOrderItemResponse();
            itemResp.setId(it.getId());
            itemResp.setInventoryItemId(it.getInventoryItem().getId());
            itemResp.setItemCode(it.getInventoryItem().getItemCode());
            itemResp.setItemName(it.getInventoryItem().getName());
            itemResp.setQuantityOrdered(it.getQuantityOrdered());
            itemResp.setUnitCost(it.getUnitCost());
            itemResp.setTotalCost(it.getTotalCost());
            return itemResp;
        }).collect(Collectors.toList()));

        return resp;
    }
}
