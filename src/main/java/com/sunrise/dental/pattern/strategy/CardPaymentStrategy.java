package com.sunrise.dental.pattern.strategy;

import com.sunrise.dental.enums.PaymentMethod;
import com.sunrise.dental.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(BigDecimal amount, String details) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Card payment amount must be greater than zero.");
        }
        return true;
    }

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.CARD;
    }
}
