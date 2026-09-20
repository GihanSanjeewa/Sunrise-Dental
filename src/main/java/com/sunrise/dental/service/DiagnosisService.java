package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.DiagnosisRequest;
import com.sunrise.dental.dto.response.DiagnosisResponse;

import java.util.List;

public interface DiagnosisService {

    DiagnosisResponse createDiagnosis(DiagnosisRequest request);

    DiagnosisResponse getDiagnosisById(Long id);

    DiagnosisResponse updateDiagnosis(Long id, DiagnosisRequest request);

    List<DiagnosisResponse> getDiagnosesByPatientId(Long patientId);

    List<DiagnosisResponse> getDiagnosesByClinicalRecordId(Long clinicalRecordId);

    void deleteDiagnosis(Long id);
}
