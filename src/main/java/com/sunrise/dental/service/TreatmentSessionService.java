package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.TreatmentSessionRequest;
import com.sunrise.dental.dto.response.TreatmentSessionResponse;
import com.sunrise.dental.enums.SessionStatus;

import java.time.LocalDate;
import java.util.List;

public interface TreatmentSessionService {

    TreatmentSessionResponse createSession(TreatmentSessionRequest request);

    TreatmentSessionResponse getSessionById(Long id);

    TreatmentSessionResponse updateSession(Long id, TreatmentSessionRequest request);

    TreatmentSessionResponse updateSessionStatus(Long id, SessionStatus status, String notes);

    List<TreatmentSessionResponse> getSessionsByTreatmentPlanId(Long treatmentPlanId);

    List<TreatmentSessionResponse> getSessionsByPatientId(Long patientId);

    List<TreatmentSessionResponse> getSessionsByDentistAndDate(Long dentistId, LocalDate date);

    void deleteSession(Long id);
}
