package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.PaymentRequest;
import com.sunrise.dental.dto.response.PaymentResponse;
import com.sunrise.dental.dto.response.ReceiptResponse;
import com.sunrise.dental.entity.Bill;
import com.sunrise.dental.entity.Payment;
import com.sunrise.dental.enums.PaymentStatus;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.pattern.factory.ReceiptFactory;
import com.sunrise.dental.pattern.strategy.PaymentStrategyContext;
import com.sunrise.dental.repository.BillRepository;
import com.sunrise.dental.repository.PaymentRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.PaymentService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final PaymentStrategyContext paymentStrategyContext;
    private final AuditService auditService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BillRepository billRepository,
                              PaymentStrategyContext paymentStrategyContext,
                              AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
        this.paymentStrategyContext = paymentStrategyContext;
        this.auditService = auditService;
    }

    @Override
    public ReceiptResponse processPayment(PaymentRequest request) {
        if (request.getBillId() == null) {
            throw new ValidationException("Bill ID cannot be empty.");
        }
        if (request.getAmountPaid() == null || request.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Payment amount must be greater than zero.");
        }
        if (request.getPaymentMethod() == null) {
            throw new ValidationException("Payment method cannot be empty.");
        }

        Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + request.getBillId()));

        if (bill.getPaymentStatus() == PaymentStatus.PAID) {
            throw new ValidationException("This bill has already been fully paid.");
        }

        // Execute Strategy Pattern for specific payment method
        paymentStrategyContext.executePayment(
                request.getPaymentMethod(),
                request.getAmountPaid(),
                request.getNotes()
        );

        Long maxId = paymentRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String paymentNumber = IdSequenceGenerator.generatePaymentNumber(nextId);

        while (paymentRepository.findByPaymentNumber(paymentNumber).isPresent()) {
            nextId++;
            paymentNumber = IdSequenceGenerator.generatePaymentNumber(nextId);
        }

        Payment payment = new Payment(
                paymentNumber,
                bill,
                request.getAmountPaid(),
                request.getPaymentMethod(),
                request.getNotes() != null ? request.getNotes().trim() : null
        );

        Payment savedPayment = paymentRepository.save(payment);

        // Update Bill Status
        List<Payment> allBillPayments = paymentRepository.findByBillId(bill.getId());
        BigDecimal totalPaidSoFar = allBillPayments.stream()
                .map(Payment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaidSoFar.compareTo(bill.getTotalAmount()) >= 0) {
            bill.setPaymentStatus(PaymentStatus.PAID);
        } else if (totalPaidSoFar.compareTo(BigDecimal.ZERO) > 0) {
            bill.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        }
        billRepository.save(bill);

        auditService.logAction("RECEPTIONIST", "RECORD_PAYMENT", "PAYMENT", savedPayment.getPaymentNumber(),
                "Recorded payment of LKR " + savedPayment.getAmountPaid() + " via " + savedPayment.getPaymentMethod() + " for Bill " + bill.getBillNumber());

        // Factory Pattern: Generate Receipt Response
        return ReceiptFactory.createReceipt(bill, savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptByBillId(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + billId));

        List<Payment> payments = paymentRepository.findByBillId(billId);
        Payment latestPayment = payments.isEmpty() ? null : payments.get(payments.size() - 1);

        return ReceiptFactory.createReceipt(bill, latestPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponse getReceiptByPaymentId(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        return ReceiptFactory.createReceipt(payment.getBill(), payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsForBill(Long billId) {
        return paymentRepository.findByBillId(billId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private PaymentResponse mapToResponse(Payment p) {
        PaymentResponse resp = new PaymentResponse();
        resp.setId(p.getId());
        resp.setPaymentNumber(p.getPaymentNumber());
        if (p.getBill() != null) {
            resp.setBillId(p.getBill().getId());
            resp.setBillNumber(p.getBill().getBillNumber());
        }
        resp.setAmountPaid(p.getAmountPaid());
        resp.setPaymentMethod(p.getPaymentMethod());
        resp.setPaymentDate(p.getPaymentDate());
        resp.setNotes(p.getNotes());
        return resp;
    }
}
