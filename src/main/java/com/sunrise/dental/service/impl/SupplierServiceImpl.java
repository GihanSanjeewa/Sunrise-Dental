package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.SupplierRequest;
import com.sunrise.dental.dto.response.SupplierResponse;
import com.sunrise.dental.entity.Supplier;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.SupplierRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.SupplierService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final AuditService auditService;

    public SupplierServiceImpl(SupplierRepository supplierRepository, AuditService auditService) {
        this.supplierRepository = supplierRepository;
        this.auditService = auditService;
    }

    @Override
    public SupplierResponse createSupplier(SupplierRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ValidationException("Supplier name is required.");
        }
        if (request.getPhone() == null || request.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone number is required.");
        }

        Long maxId = supplierRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String supplierCode = IdSequenceGenerator.generateSupplierCode(nextId);

        while (supplierRepository.existsBySupplierCode(supplierCode)) {
            nextId++;
            supplierCode = IdSequenceGenerator.generateSupplierCode(nextId);
        }

        Supplier supplier = new Supplier(
                supplierCode,
                request.getName().trim(),
                request.getContactPerson(),
                request.getPhone().trim(),
                request.getEmail(),
                request.getAddress(),
                request.getStatus() != null ? request.getStatus() : "ACTIVE"
        );

        Supplier saved = supplierRepository.save(supplier);
        auditService.logAction("ADMIN", "CREATE", "SUPPLIER", saved.getSupplierCode(),
                "Registered supplier: " + saved.getName());

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
        return mapToResponse(supplier);
    }

    @Override
    public SupplierResponse updateSupplier(Long id, SupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        if (request.getName() != null) supplier.setName(request.getName().trim());
        if (request.getContactPerson() != null) supplier.setContactPerson(request.getContactPerson());
        if (request.getPhone() != null) supplier.setPhone(request.getPhone().trim());
        if (request.getEmail() != null) supplier.setEmail(request.getEmail());
        if (request.getAddress() != null) supplier.setAddress(request.getAddress());
        if (request.getStatus() != null) supplier.setStatus(request.getStatus());

        Supplier updated = supplierRepository.save(supplier);
        auditService.logAction("ADMIN", "UPDATE", "SUPPLIER", updated.getSupplierCode(),
                "Updated supplier: " + updated.getName());

        return mapToResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> getActiveSuppliers() {
        return supplierRepository.findByStatus("ACTIVE").stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
        supplierRepository.delete(supplier);
        auditService.logAction("ADMIN", "DELETE", "SUPPLIER", supplier.getSupplierCode(),
                "Deleted supplier: " + supplier.getName());
    }

    private SupplierResponse mapToResponse(Supplier s) {
        SupplierResponse resp = new SupplierResponse();
        resp.setId(s.getId());
        resp.setSupplierCode(s.getSupplierCode());
        resp.setName(s.getName());
        resp.setContactPerson(s.getContactPerson());
        resp.setPhone(s.getPhone());
        resp.setEmail(s.getEmail());
        resp.setAddress(s.getAddress());
        resp.setStatus(s.getStatus());
        resp.setCreatedAt(s.getCreatedAt());
        resp.setUpdatedAt(s.getUpdatedAt());
        return resp;
    }
}
