package com.devtrace.platform.service;

import com.devtrace.platform.dto.Evidence;
import com.devtrace.platform.dto.InvestigationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TelemetrySanitizationService {

    private static final List<String> SENSITIVE_KEYWORDS = List.of(
            "password", "secret", "token", "apikey", "api_key", "authorization", "bearer", "creditcard"
    );

    public InvestigationContext sanitize(InvestigationContext context) {
        if (context == null) return null;

        if (context.getEvidence() != null) {
            List<Evidence> sanitizedEvidence = context.getEvidence().stream()
                    .map(this::sanitizeEvidence)
                    .collect(Collectors.toList());
            context.setEvidence(sanitizedEvidence);
        }

        return context;
    }

    private Evidence sanitizeEvidence(Evidence evidence) {
        String message = evidence.getMessage();
        if (message != null) {
            // Mask Key-Value pairs
            for (String keyword : SENSITIVE_KEYWORDS) {
                message = message.replaceAll("(?i)" + keyword + "['\"]?\\s*[:=]\\s*['\"]?[^\\s,'\"]+['\"]?", keyword + "=[REDACTED]");
            }
            // Mask Credit Cards (13-19 digits)
            message = message.replaceAll("\\b(?:\\d[ -]*?){13,16}\\b", "[CREDIT_CARD_REDACTED]");
            // Mask Authorization headers (Bearer xxxx)
            message = message.replaceAll("(?i)Bearer\\s+[a-zA-Z0-9\\-_\\.]+", "Bearer [REDACTED]");
            // Mask UUIDs (common for session ids)
            message = message.replaceAll("\\b[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\b", "[UUID_REDACTED]");
        }
        return new Evidence(evidence.getType(), evidence.getSourceSpan(), evidence.getService(), message, evidence.getTimestamp());
    }
}
