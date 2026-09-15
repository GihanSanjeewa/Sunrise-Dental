package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.TreatmentRequest;
import com.sunrise.dental.dto.response.TreatmentResponse;

import java.util.List;

public interface TreatmentService {
    TreatmentResponse addTreatment(TreatmentRequest request);
    TreatmentResponse updateTreatment(Long id, TreatmentRequest request);
    TreatmentResponse getTreatmentById(Long id);
    TreatmentResponse getTreatmentByCode(String code);
    List<TreatmentResponse> getAllTreatments();
    List<TreatmentResponse> getActiveTreatments();
}
