package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.SupplierRequest;
import com.sunrise.dental.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {

    SupplierResponse createSupplier(SupplierRequest request);

    SupplierResponse getSupplierById(Long id);

    SupplierResponse updateSupplier(Long id, SupplierRequest request);

    List<SupplierResponse> getAllSuppliers();

    List<SupplierResponse> getActiveSuppliers();

    void deleteSupplier(Long id);
}
