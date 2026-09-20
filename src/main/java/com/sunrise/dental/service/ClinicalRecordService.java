package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.ClinicalRecordRequest;
import com.sunrise.dental.dto.response.ClinicalRecordResponse;

import java.time.LocalDate;
import java.util.List;

public interface ClinicalRecordService {

    ClinicalRecordResponse createClinicalRecord(ClinicalRecordRequest request);

    ClinicalRecordResponse getClinicalRecordById(Long id);

    ClinicalRecordResponse getClinicalRecordByNumber(String recordNumber);

    ClinicalRecordResponse updateClinicalRecord(Long id, ClinicalRecordRequest request);

    List<ClinicalRecordResponse> getClinicalRecordsByPatientId(Long patientId);

    List<ClinicalRecordResponse> getClinicalRecordsByDentistId(Long dentistId);

    List<ClinicalRecordResponse> getClinicalRecordsByDate(LocalDate date);

    void deleteClinicalRecord(Long id);
}
