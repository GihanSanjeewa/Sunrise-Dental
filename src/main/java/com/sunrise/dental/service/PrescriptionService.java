package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.MedicationRequest;
import com.sunrise.dental.dto.request.PrescriptionRequest;
import com.sunrise.dental.dto.response.MedicationResponse;
import com.sunrise.dental.dto.response.PrescriptionResponse;

import java.util.List;

public interface PrescriptionService {

    PrescriptionResponse createPrescription(PrescriptionRequest request);

    PrescriptionResponse getPrescriptionById(Long id);

    PrescriptionResponse getPrescriptionByNumber(String prescriptionNumber);

    List<PrescriptionResponse> getPrescriptionsByPatientId(Long patientId);

    List<PrescriptionResponse> getPrescriptionsByDentistId(Long dentistId);

    List<MedicationResponse> getAllMedications();

    List<MedicationResponse> searchMedications(String query);

    MedicationResponse createMedication(MedicationRequest request);

    void deletePrescription(Long id);
}
