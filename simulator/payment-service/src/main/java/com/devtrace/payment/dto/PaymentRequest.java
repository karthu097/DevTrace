package com.devtrace.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        String orderId,
        BigDecimal amount,
        String paymentMethod
) {}
