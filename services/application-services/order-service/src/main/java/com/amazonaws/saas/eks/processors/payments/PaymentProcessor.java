package com.amazonaws.saas.eks.processors.payments;

import java.math.BigDecimal;

public interface PaymentProcessor {
    void process(String tenantId, String orderNumber, BigDecimal amount);
}
