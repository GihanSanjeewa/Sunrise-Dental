package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.BillRequest;
import com.sunrise.dental.dto.response.BillResponse;

import java.math.BigDecimal;
import java.util.List;

public interface BillingService {
    BillResponse generateBill(BillRequest request);
    BillResponse calculateBill(Long appointmentId, BigDecimal discountPercentage, BigDecimal taxPercentage);
    BillResponse getBillById(Long id);
    BillResponse getBillByAppointmentId(Long appointmentId);
    BillResponse getBillByNumber(String billNumber);
    List<BillResponse> getAllBills();
    List<BillResponse> getPendingBills();
}
