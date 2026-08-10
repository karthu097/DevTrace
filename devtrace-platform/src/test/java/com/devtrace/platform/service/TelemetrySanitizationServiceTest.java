package com.devtrace.platform.service;

import com.devtrace.platform.dto.Evidence;
import com.devtrace.platform.dto.InvestigationContext;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TelemetrySanitizationServiceTest {

    private final TelemetrySanitizationService sanitizationService = new TelemetrySanitizationService();

    @Test
    void testSanitizeEvidenceMasksCreditCardAndPasswords() {
        InvestigationContext context = new InvestigationContext();
        context.setEvidence(List.of(
            new Evidence("LOG", "span1", "service1", "User logged in. password=secret123, card: 4111-1111-1111-1111", null),
            new Evidence("HTTP", "span2", "service2", "Failed Request. Headers: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xyz, session_id=550e8400-e29b-41d4-a716-446655440000", null)
        ));

        InvestigationContext sanitized = sanitizationService.sanitize(context);

        String msg1 = sanitized.getEvidence().get(0).getMessage();
        assertTrue(msg1.contains("password=[REDACTED]"));
        assertTrue(msg1.contains("[CREDIT_CARD_REDACTED]"));

        String msg2 = sanitized.getEvidence().get(1).getMessage();
        assertTrue(msg2.contains("Bearer [REDACTED]"));
        assertTrue(msg2.contains("[UUID_REDACTED]"));
    }
}
