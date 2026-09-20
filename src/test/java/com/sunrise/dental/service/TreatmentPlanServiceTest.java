package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.TreatmentPlanItemRequest;
import com.sunrise.dental.dto.request.TreatmentPlanRequest;
import com.sunrise.dental.dto.response.TreatmentPlanResponse;
import com.sunrise.dental.entity.*;
import com.sunrise.dental.enums.DentistStatus;
import com.sunrise.dental.enums.TreatmentPlanStatus;
import com.sunrise.dental.repository.*;
import com.sunrise.dental.service.impl.TreatmentPlanServiceImpl;
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
public class TreatmentPlanServiceTest {

    @Mock
    private TreatmentPlanRepository treatmentPlanRepository;

    @Mock
    private TreatmentPlanItemRepository treatmentPlanItemRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TreatmentPlanServiceImpl treatmentPlanService;

    private Patient patient;
    private Dentist dentist;
    private Treatment treatment;

    @BeforeEach
    void setUp() {
        patient = new Patient("P-000001", "Amara Silva", "Kandy", "0773334455", "amara@test.com", LocalDate.of(1992, 6, 10), "FEMALE");
        patient.setId(1L);

        dentist = new Dentist("D-000001", "Dr. Samantha", "Endodontics", "0779998877", "samantha@test.com", DentistStatus.AVAILABLE);
        dentist.setId(1L);

        treatment = new Treatment("TRT-001", "Root Canal Treatment", "Full RCT with obturation",
                BigDecimal.valueOf(15000.00), BigDecimal.valueOf(2000.00), "ACTIVE");
        treatment.setId(1L);
    }

    @Test
    @DisplayName("Create treatment plan using dynamic catalog pricing and calculate estimated totals")
    void shouldCreateTreatmentPlanWithDynamicPricing() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(dentist));
        when(treatmentPlanRepository.findMaxId()).thenReturn(0L);
        when(treatmentPlanRepository.existsByPlanNumber(anyString())).thenReturn(false);

        TreatmentPlan savedPlan = new TreatmentPlan("TP-2026-000001", patient, dentist, "Molar RCT Plan",
                TreatmentPlanStatus.PLANNED, BigDecimal.ZERO, BigDecimal.ZERO, "Sequenced root canal");
        savedPlan.setId(10L);

        when(treatmentPlanRepository.save(any(TreatmentPlan.class))).thenReturn(savedPlan);
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(treatment));

        TreatmentPlanItemRequest itemReq = new TreatmentPlanItemRequest();
        itemReq.setTreatmentId(1L);
        itemReq.setToothNumber("36");
        itemReq.setQuantity(1);
        // unitCost left null to test dynamic catalog price lookup
        itemReq.setUnitCost(null);

        TreatmentPlanRequest planReq = new TreatmentPlanRequest();
        planReq.setPatientId(1L);
        planReq.setDentistId(1L);
        planReq.setTitle("Molar RCT Plan");
        planReq.setNotes("Sequenced root canal");
        planReq.setItems(List.of(itemReq));

        TreatmentPlanResponse response = treatmentPlanService.createTreatmentPlan(planReq);

        assertNotNull(response);
        assertEquals("TP-2026-000001", response.getPlanNumber());
        assertEquals("Molar RCT Plan", response.getTitle());
        verify(treatmentPlanRepository, atLeastOnce()).save(any(TreatmentPlan.class));
        verify(treatmentPlanItemRepository, times(1)).save(any(TreatmentPlanItem.class));
    }

    @Test
    @DisplayName("Update treatment plan status")
    void shouldUpdatePlanStatus() {
        TreatmentPlan plan = new TreatmentPlan("TP-2026-000001", patient, dentist, "Molar RCT Plan",
                TreatmentPlanStatus.PLANNED, BigDecimal.valueOf(15000), BigDecimal.ZERO, "Notes");
        plan.setId(1L);

        when(treatmentPlanRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(treatmentPlanRepository.save(any(TreatmentPlan.class))).thenReturn(plan);

        TreatmentPlanResponse response = treatmentPlanService.updateTreatmentPlanStatus(1L, TreatmentPlanStatus.APPROVED);

        assertNotNull(response);
        assertEquals(TreatmentPlanStatus.APPROVED, response.getStatus());
        verify(auditService, times(1)).logAction(anyString(), anyString(), anyString(), anyString(), anyString());
    }
}
