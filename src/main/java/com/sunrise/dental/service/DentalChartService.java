package com.sunrise.dental.service;

import com.sunrise.dental.dto.request.ToothHistoryRequest;
import com.sunrise.dental.dto.request.ToothRecordRequest;
import com.sunrise.dental.dto.response.DentalChartResponse;
import com.sunrise.dental.dto.response.ToothHistoryResponse;
import com.sunrise.dental.dto.response.ToothRecordResponse;

import java.util.List;

public interface DentalChartService {

    DentalChartResponse getDentalChartByPatientId(Long patientId);

    ToothRecordResponse updateToothCondition(ToothRecordRequest request);

    ToothHistoryResponse addToothHistory(ToothHistoryRequest request);

    List<ToothHistoryResponse> getToothHistory(Long dentalToothId);

    void initializePatientChart(Long patientId);
}
