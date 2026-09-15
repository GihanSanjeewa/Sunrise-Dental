package com.sunrise.dental.pattern.strategy;

import com.sunrise.dental.enums.PaymentMethod;
import java.math.BigDecimal;

/**
 * Strategy interface defining payment validation and processing operations.
 */
public interface PaymentStrategy {
    boolean processPayment(BigDecimal amount, String details);
    PaymentMethod getSupportedMethod();
}
