package com.sunrise.dental.service;

import com.sunrise.dental.dto.response.NoShowRiskResponse;

public interface NoShowPredictionService {

    NoShowRiskResponse assessNoShowRisk(Long appointmentId);
}
