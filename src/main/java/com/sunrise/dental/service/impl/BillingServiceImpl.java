package com.sunrise.dental.service.impl;

import com.sunrise.dental.dto.request.BillRequest;
import com.sunrise.dental.dto.response.BillResponse;
import com.sunrise.dental.entity.Appointment;
import com.sunrise.dental.entity.Bill;
import com.sunrise.dental.entity.Treatment;
import com.sunrise.dental.enums.PaymentStatus;
import com.sunrise.dental.exception.DuplicateResourceException;
import com.sunrise.dental.exception.ResourceNotFoundException;
import com.sunrise.dental.exception.ValidationException;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.repository.BillRepository;
import com.sunrise.dental.service.AuditService;
import com.sunrise.dental.service.BillingService;
import com.sunrise.dental.util.IdSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillingServiceImpl implements BillingService {

    private final BillRepository billRepository;
    private final AppointmentRepository appointmentRepository;
    private final AuditService auditService;

    public BillingServiceImpl(BillRepository billRepository,
                              AppointmentRepository appointmentRepository,
                              AuditService auditService) {
        this.billRepository = billRepository;
        this.appointmentRepository = appointmentRepository;
        this.auditService = auditService;
    }

    @Override
    public BillResponse generateBill(BillRequest request) {
        if (request.getAppointmentId() == null) {
            throw new ValidationException("Appointment ID cannot be empty.");
        }

        if (billRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new DuplicateResourceException("A bill has already been generated for this appointment.");
        }

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + request.getAppointmentId()));

        Treatment treatment = appointment.getTreatment();
        if (treatment == null) {
            throw new ValidationException("Appointment does not have an associated treatment catalog entry.");
        }

        BigDecimal consultationFee = treatment.getConsultationFee() != null ? treatment.getConsultationFee() : BigDecimal.ZERO;
        BigDecimal treatmentCost = treatment.getTreatmentCost() != null ? treatment.getTreatmentCost() : BigDecimal.ZERO;

        // Dynamic Calculation
        BigDecimal subtotal = consultationFee.add(treatmentCost);

        BigDecimal discountPct = request.getDiscountPercentage() != null ? request.getDiscountPercentage() : BigDecimal.ZERO;
        BigDecimal discountAmount = subtotal.multiply(discountPct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        BigDecimal taxPct = request.getTaxPercentage() != null ? request.getTaxPercentage() : BigDecimal.ZERO;
        BigDecimal taxAmount = taxableAmount.multiply(taxPct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = taxableAmount.add(taxAmount);

        Long maxId = billRepository.findMaxId();
        long nextId = (maxId != null ? maxId : 0L) + 1L;
        String billNumber = IdSequenceGenerator.generateBillNumber(nextId);

        while (billRepository.existsByBillNumber(billNumber)) {
            nextId++;
            billNumber = IdSequenceGenerator.generateBillNumber(nextId);
        }

        Bill bill = new Bill(
                billNumber,
                appointment,
                consultationFee,
                treatmentCost,
                subtotal,
                discountPct,
                discountAmount,
                taxPct,
                taxAmount,
                totalAmount,
                PaymentStatus.PENDING
        );

        Bill saved = billRepository.save(bill);
        auditService.logAction("RECEPTIONIST", "GENERATE_BILL", "BILL", saved.getBillNumber(),
                "Generated invoice for appointment " + appointment.getAppointmentNumber() + " with total amount: " + totalAmount);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse calculateBill(Long appointmentId, BigDecimal discountPercentage, BigDecimal taxPercentage) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        Treatment treatment = appointment.getTreatment();
        BigDecimal consultationFee = treatment.getConsultationFee() != null ? treatment.getConsultationFee() : BigDecimal.ZERO;
        BigDecimal treatmentCost = treatment.getTreatmentCost() != null ? treatment.getTreatmentCost() : BigDecimal.ZERO;

        BigDecimal subtotal = consultationFee.add(treatmentCost);
        BigDecimal discountPct = discountPercentage != null ? discountPercentage : BigDecimal.ZERO;
        BigDecimal discountAmount = subtotal.multiply(discountPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
            taxableAmount = BigDecimal.ZERO;
        }

        BigDecimal taxPct = taxPercentage != null ? taxPercentage : BigDecimal.ZERO;
        BigDecimal taxAmount = taxableAmount.multiply(taxPct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalAmount = taxableAmount.add(taxAmount);

        BillResponse preview = new BillResponse();
        preview.setAppointmentId(appointment.getId());
        preview.setAppointmentNumber(appointment.getAppointmentNumber());
        if (appointment.getPatient() != null) {
            preview.setPatientNumber(appointment.getPatient().getPatientNumber());
            preview.setPatientName(appointment.getPatient().getFullName());
        }
        if (appointment.getDentist() != null) {
            preview.setDentistName(appointment.getDentist().getName());
        }
        if (treatment != null) {
            preview.setTreatmentName(treatment.getTreatmentName());
        }
        preview.setConsultationFee(consultationFee);
        preview.setTreatmentCost(treatmentCost);
        preview.setSubtotal(subtotal);
        preview.setDiscountPercentage(discountPct);
        preview.setDiscountAmount(discountAmount);
        preview.setTaxPercentage(taxPct);
        preview.setTaxAmount(taxAmount);
        preview.setTotalAmount(totalAmount);
        preview.setPaymentStatus(PaymentStatus.PENDING);

        return preview;
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));
        return mapToResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillByAppointmentId(Long appointmentId) {
        Bill bill = billRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("No bill found for appointment ID: " + appointmentId));
        return mapToResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public BillResponse getBillByNumber(String billNumber) {
        Bill bill = billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("No bill found with bill number: " + billNumber));
        return mapToResponse(bill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getAllBills() {
        return billRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillResponse> getPendingBills() {
        return billRepository.findByPaymentStatus(PaymentStatus.PENDING).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private BillResponse mapToResponse(Bill b) {
        BillResponse resp = new BillResponse();
        resp.setId(b.getId());
        resp.setBillNumber(b.getBillNumber());
        if (b.getAppointment() != null) {
            resp.setAppointmentId(b.getAppointment().getId());
            resp.setAppointmentNumber(b.getAppointment().getAppointmentNumber());
            if (b.getAppointment().getPatient() != null) {
                resp.setPatientNumber(b.getAppointment().getPatient().getPatientNumber());
                resp.setPatientName(b.getAppointment().getPatient().getFullName());
            }
            if (b.getAppointment().getDentist() != null) {
                resp.setDentistName(b.getAppointment().getDentist().getName());
            }
            if (b.getAppointment().getTreatment() != null) {
                resp.setTreatmentName(b.getAppointment().getTreatment().getTreatmentName());
            }
        }
        resp.setConsultationFee(b.getConsultationFee());
        resp.setTreatmentCost(b.getTreatmentCost());
        resp.setSubtotal(b.getSubtotal());
        resp.setDiscountPercentage(b.getDiscountPercentage());
        resp.setDiscountAmount(b.getDiscountAmount());
        resp.setTaxPercentage(b.getTaxPercentage());
        resp.setTaxAmount(b.getTaxAmount());
        resp.setTotalAmount(b.getTotalAmount());
        resp.setPaymentStatus(b.getPaymentStatus());
        resp.setCreatedAt(b.getCreatedAt());
        return resp;
    }
}
