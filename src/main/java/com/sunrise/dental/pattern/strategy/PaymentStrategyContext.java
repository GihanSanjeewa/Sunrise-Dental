package com.sunrise.dental.pattern.strategy;

import com.sunrise.dental.enums.PaymentMethod;
import com.sunrise.dental.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PaymentStrategyContext {

    private final Map<PaymentMethod, PaymentStrategy> strategyMap = new EnumMap<>(PaymentMethod.class);

    public PaymentStrategyContext(List<PaymentStrategy> strategies) {
        for (PaymentStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedMethod(), strategy);
        }
    }

    public boolean executePayment(PaymentMethod method, BigDecimal amount, String details) {
        PaymentStrategy strategy = strategyMap.get(method);
        if (strategy == null) {
            throw new ValidationException("Unsupported payment method: " + method);
        }
        return strategy.processPayment(amount, details);
    }
}
