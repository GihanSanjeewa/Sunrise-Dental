package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.BillRequest;
import com.sunrise.dental.dto.response.BillResponse;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.enums.AppointmentStatus;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.enums.PaymentStatus;
import com.sunrise.dental.exception.DuplicateResourceException;
import com.sunrise.dental.repository.AppointmentRepository;
import com.sunrise.dental.repository.BillRepository;
import com.sunrise.dental.service.impl.BillingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {

    @Mock
    private BillRepository billRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BillingServiceImpl billingService;

    private Appointment mockAppointment;

    @BeforeEach
    void setUp() {
        Patient patient = new Patient("P-000001", "Sunil Wickramasinghe", "Colombo", "0771234567", "sunil@gmail.com", LocalDate.of(1990, 1, 1), "MALE");
        patient.setId(1L);

        Dentist dentist = new Dentist("D-000001", "Dr. Rohan Perera", "Orthodontics", "0772221100", "rohan@sunrisedental.lk", DentistStatus.AVAILABLE);
        dentist.setId(1L);

        // Treatment: Consultation Fee = 2000.00, Treatment Cost = 6000.00
        Treatment treatment = new Treatment("TRT-004", "Surgical Tooth Extraction", "Extraction",
                BigDecimal.valueOf(6000.00), BigDecimal.valueOf(2000.00), "ACTIVE");
        treatment.setId(4L);

        mockAppointment = new Appointment("APT-2026-000005", patient, dentist, treatment,
                LocalDate.now(), LocalTime.of(9, 30), AppointmentStatus.COMPLETED, "Extraction completed");
        mockAppointment.setId(5L);
    }

    @Test
    @DisplayName("TC009 / TDD: Accurately calculate bill subtotal, discount, tax, and total amount")
    void shouldCalculateCorrectBillWithDiscountAndTax() {
        when(billRepository.existsByAppointmentId(5L)).thenReturn(false);
        when(appointmentRepository.findById(5L)).thenReturn(Optional.of(mockAppointment));
        when(billRepository.findMaxId()).thenReturn(1L);
        when(billRepository.existsByBillNumber(anyString())).thenReturn(false);

        // Subtotal = 2000 + 6000 = 8000.00
        // Discount 5% = 400.00
        // Taxable = 7600.00
        // Tax 2.5% = 190.00
        // Total = 7600 + 190 = 7790.00
        BillRequest request = new BillRequest(5L, BigDecimal.valueOf(5.00), BigDecimal.valueOf(2.50));

        when(billRepository.save(any(Bill.class))).thenAnswer(invocation -> {
            Bill saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });

        BillResponse response = billingService.generateBill(request);

        assertNotNull(response);
        assertEquals(0, BigDecimal.valueOf(2000.00).compareTo(response.getConsultationFee()));
        assertEquals(0, BigDecimal.valueOf(6000.00).compareTo(response.getTreatmentCost()));
        assertEquals(0, BigDecimal.valueOf(8000.00).compareTo(response.getSubtotal()));
        assertEquals(0, BigDecimal.valueOf(400.00).compareTo(response.getDiscountAmount()));
        assertEquals(0, BigDecimal.valueOf(190.00).compareTo(response.getTaxAmount()));
        assertEquals(0, BigDecimal.valueOf(7790.00).compareTo(response.getTotalAmount()));
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());
    }

    @Test
    @DisplayName("TC016: Reject duplicate bill generation for the same appointment")
    void shouldRejectDuplicateBillGeneration() {
        when(billRepository.existsByAppointmentId(5L)).thenReturn(true);

        BillRequest request = new BillRequest(5L, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThrows(DuplicateResourceException.class, () -> {
            billingService.generateBill(request);
        });

        verify(billRepository, never()).save(any(Bill.class));
    }
}
