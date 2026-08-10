package com.devtrace.payment.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        String service,
        String errorCode,
        String message,
        String requestId
) {
}
