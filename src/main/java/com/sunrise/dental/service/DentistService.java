package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.DentistRequest;
import com.sunrise.dental.dto.response.AppointmentResponse;
import com.sunrise.dental.dto.response.DentistResponse;
import com.sunrise.dental.enums.DentistStatus;

import java.util.List;

public interface DentistService {
    DentistResponse addDentist(DentistRequest request);
    DentistResponse updateDentist(Long id, DentistRequest request);
    DentistResponse getDentistById(Long id);
    DentistResponse updateAvailabilityStatus(Long id, DentistStatus status);
    List<DentistResponse> getAllDentists();
    List<DentistResponse> getAvailableDentists();
    List<DentistResponse> searchDentists(String query);
    List<AppointmentResponse> getDentistAppointments(Long dentistId);
}
