package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.PatientRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.dto.response.PatientResponse;

import java.util.List;

public interface PatientService {
    PatientResponse registerPatient(PatientRequest request);
    PatientResponse getPatientById(Long id);
    PatientResponse getPatientByNumber(String patientNumber);
    PatientResponse updatePatient(Long id, PatientRequest request);
    List<PatientResponse> getAllPatients();
    List<PatientResponse> searchPatients(String query);
    List<AppointmentResponse> getPatientAppointmentHistory(Long patientId);
}
