package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.TreatmentPlanItemRequest;
import com.sunrise.dental.dto.request.TreatmentPlanRequest;
import com.sunrise.dental.dto.response.TreatmentPlanItemResponse;
import com.sunrise.dental.dto.response.TreatmentPlanResponse;
import com.sunrise.dental.enums.TreatmentItemStatus;
import com.sunrise.dental.enums.TreatmentPlanStatus;

import java.util.List;

public interface TreatmentPlanService {

    TreatmentPlanResponse createTreatmentPlan(TreatmentPlanRequest request);

    TreatmentPlanResponse getTreatmentPlanById(Long id);

    TreatmentPlanResponse getTreatmentPlanByNumber(String planNumber);

    TreatmentPlanResponse updateTreatmentPlan(Long id, TreatmentPlanRequest request);

    TreatmentPlanResponse updateTreatmentPlanStatus(Long id, TreatmentPlanStatus status);

    TreatmentPlanItemResponse addItemToPlan(Long planId, TreatmentPlanItemRequest itemRequest);

    TreatmentPlanItemResponse updateItemStatus(Long itemId, TreatmentItemStatus status);

    void removeItemFromPlan(Long planId, Long itemId);

    List<TreatmentPlanResponse> getTreatmentPlansByPatientId(Long patientId);

    List<TreatmentPlanResponse> getTreatmentPlansByDentistId(Long dentistId);

    List<TreatmentPlanResponse> getTreatmentPlansByStatus(TreatmentPlanStatus status);

    void deleteTreatmentPlan(Long id);
}
