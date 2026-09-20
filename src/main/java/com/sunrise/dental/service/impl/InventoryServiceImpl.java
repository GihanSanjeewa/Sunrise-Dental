package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.InventoryItemRequest;
import com.sunrise.dental.dto.request.StockAdjustmentRequest;
import com.sunrise.dental.dto.response.InventoryItemResponse;
import com.sunrise.dental.dto.response.InventoryTransactionResponse;
import com.sunrise.dental.entity.InventoryItem;
import com.sunrise.dental.entity.InventoryTransaction;
import com.sunrise.dental.entity.Supplier;
import com.sunrise.dental.enums.TransactionType;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.InventoryItemRepository;
import com.sunrise.dental.repository.InventoryTransactionRepository;
import com.sunrise.dental.repository.SupplierRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.InventoryService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final SupplierRepository supplierRepository;
    private final AuditService auditService;

    public InventoryServiceImpl(InventoryItemRepository inventoryItemRepository,
                                InventoryTransactionRepository inventoryTransactionRepository,
                                SupplierRepository supplierRepository,
                                AuditService auditService) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.supplierRepository = supplierRepository;
        this.auditService = auditService;
    }

    @Override
    public InventoryItemResponse createItem(InventoryItemRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Item name cannot be empty.");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            throw new ValidationException("Category cannot be empty.");
        }
        if (request.getUnit() == null || request.getUnit().trim().isEmpty()) {
            throw new ValidationException("Unit cannot be empty.");
        }

        Supplier supplier = null;
        if (request.getSupplierId() != null) {
            supplier = supplierRepository.findById(request.getSupplierId()).orElse(null);
        }

        Long maxId = inventoryItemRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String itemCode = IdSequenceGenerator.generateInventoryItemCode(nextId);

        while (inventoryItemRepository.existsByItemCode(itemCode)) {
            nextId++;
            itemCode = IdSequenceGenerator.generateInventoryItemCode(nextId);
        }

        InventoryItem item = new InventoryItem(
                itemCode,
                request.getName().trim(),
                request.getCategory().trim(),
                supplier,
                request.getCurrentQuantity() != null ? request.getCurrentQuantity() : 0,
                request.getMinimumQuantity() != null ? request.getMinimumQuantity() : 10,
                request.getUnit().trim(),
                request.getUnitCost() != null ? request.getUnitCost() : BigDecimal.ZERO,
                request.getExpiryDate()
        );

        InventoryItem saved = inventoryItemRepository.save(item);

        // Record initial inventory transaction if quantity > 0
        if (saved.getCurrentQuantity() > 0) {
            InventoryTransaction tx = new InventoryTransaction(
                    saved,
                    TransactionType.ADJUSTMENT,
                    saved.getCurrentQuantity(),
                    "SYSTEM",
                    "Initial stock entry"
            );
            inventoryTransactionRepository.save(tx);
        }

        auditService.logAction("ADMIN", "CREATE", "INVENTORY_ITEM", saved.getItemCode(),
                "Created inventory item: " + saved.getName() + " (Stock: " + saved.getCurrentQuantity() + ")");

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryItemResponse getItemById(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + id));
        return mapToResponse(item);
    }

    @Override
    public InventoryItemResponse updateItem(Long id, InventoryItemRequest request) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + id));

        if (request.getName() != null) item.setName(request.getName().trim());
        if (request.getCategory() != null) item.setCategory(request.getCategory().trim());
        if (request.getUnit() != null) item.setUnit(request.getUnit().trim());
        if (request.getMinimumQuantity() != null) item.setMinimumQuantity(request.getMinimumQuantity());
        if (request.getUnitCost() != null) item.setUnitCost(request.getUnitCost());
        if (request.getExpiryDate() != null) item.setExpiryDate(request.getExpiryDate());

        if (request.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId()).orElse(null);
            item.setSupplier(supplier);
        }

        item.updateStatus();
        InventoryItem updated = inventoryItemRepository.save(item);
        auditService.logAction("ADMIN", "UPDATE", "INVENTORY_ITEM", updated.getItemCode(),
                "Updated inventory item " + updated.getName());

        return mapToResponse(updated);
    }

    @Override
    public InventoryItemResponse adjustStock(StockAdjustmentRequest request, String performedBy) {
        if (request.getInventoryItemId() == null) {
            throw new ValidationException("Inventory item ID is required.");
        }
        if (request.getTransactionType() == null) {
            throw new ValidationException("Transaction type is required.");
        }
        if (request.getQuantity() == null || request.getQuantity() == 0) {
            throw new ValidationException("Quantity must be non-zero.");
        }

        InventoryItem item = inventoryItemRepository.findById(request.getInventoryItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + request.getInventoryItemId()));

        int oldQty = item.getCurrentQuantity();
        int newQty = oldQty;

        switch (request.getTransactionType()) {
            case PURCHASE:
            case RETURN:
                newQty = oldQty + Math.abs(request.getQuantity());
                break;
            case USAGE:
            case EXPIRED:
                if (oldQty < Math.abs(request.getQuantity())) {
                    throw new ValidationException("Insufficient stock. Available: " + oldQty + ", Requested: " + Math.abs(request.getQuantity()));
                }
                newQty = oldQty - Math.abs(request.getQuantity());
                break;
            case ADJUSTMENT:
                newQty = request.getQuantity() >= 0 ? request.getQuantity() : 0;
                break;
        }

        item.setCurrentQuantity(newQty);
        InventoryItem saved = inventoryItemRepository.save(item);

        InventoryTransaction tx = new InventoryTransaction(
                saved,
                request.getTransactionType(),
                request.getQuantity(),
                performedBy != null ? performedBy : "STAFF",
                request.getReason() != null ? request.getReason() : "Stock movement: " + request.getTransactionType()
        );
        inventoryTransactionRepository.save(tx);

        auditService.logAction("STAFF", "STOCK_ADJUST", "INVENTORY_ITEM", saved.getItemCode(),
                request.getTransactionType() + " of " + request.getQuantity() + " units. Quantity changed from " + oldQty + " to " + newQty);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getAllItems() {
        return inventoryItemRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getLowStockItems() {
        return inventoryItemRepository.findLowStockItems().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getItemsByCategory(String category) {
        return inventoryItemRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getTransactionsByItemId(Long itemId) {
        return inventoryTransactionRepository.findByInventoryItemIdOrderByTransactionDateDesc(itemId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with ID: " + id));
        inventoryItemRepository.delete(item);
        auditService.logAction("ADMIN", "DELETE", "INVENTORY_ITEM", item.getItemCode(),
                "Deleted inventory item " + item.getName());
    }

    private InventoryItemResponse mapToResponse(InventoryItem it) {
        InventoryItemResponse resp = new InventoryItemResponse();
        resp.setId(it.getId());
        resp.setItemCode(it.getItemCode());
        resp.setName(it.getName());
        resp.setCategory(it.getCategory());
        if (it.getSupplier() != null) {
            resp.setSupplierId(it.getSupplier().getId());
            resp.setSupplierName(it.getSupplier().getName());
        }
        resp.setCurrentQuantity(it.getCurrentQuantity());
        resp.setMinimumQuantity(it.getMinimumQuantity());
        resp.setUnit(it.getUnit());
        resp.setUnitCost(it.getUnitCost());
        resp.setExpiryDate(it.getExpiryDate());
        resp.setStatus(it.getStatus());
        resp.setIsLowStock(it.getCurrentQuantity() <= it.getMinimumQuantity());
        resp.setCreatedAt(it.getCreatedAt());
        resp.setUpdatedAt(it.getUpdatedAt());
        return resp;
    }

    private InventoryTransactionResponse mapToTransactionResponse(InventoryTransaction tx) {
        InventoryTransactionResponse resp = new InventoryTransactionResponse();
        resp.setId(tx.getId());
        resp.setInventoryItemId(tx.getInventoryItem().getId());
        resp.setItemCode(tx.getInventoryItem().getItemCode());
        resp.setItemName(tx.getInventoryItem().getName());
        resp.setTransactionType(tx.getTransactionType());
        resp.setQuantity(tx.getQuantity());
        resp.setPerformedBy(tx.getPerformedBy());
        resp.setReason(tx.getReason());
        resp.setTransactionDate(tx.getTransactionDate());
        return resp;
    }
}
