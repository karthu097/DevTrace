package com.devtrace.platform.service;

import com.devtrace.platform.dto.IncidentReport;
import com.devtrace.platform.dto.InvestigationContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ConditionalOnProperty(name = "devtrace.ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAIProvider implements AIProvider {

    @Override
    public IncidentReport generateInvestigationReport(InvestigationContext context) {
        IncidentReport report = new IncidentReport();
        report.setTraceId(context.getTraceId());
        report.setSeverity("HIGH");
        report.setIncidentSummary("Order creation failed because the payment provider exceeded the payment service timeout.");
        
        report.setRootCause(context.getDeterministicRootCause());
        
        report.setWhatHappened(List.of(
                "The API Gateway received the request.",
                "Order Service began processing.",
                "Payment Service called the Payment Provider.",
                "The provider took longer than the configured timeout.",
                "Payment Service timed out.",
                "The failure propagated back to the Order Service and API Gateway."
        ));
        
        report.setEvidence(List.of(
                "Payment Provider span exceeded expected duration.",
                "Payment Service recorded a TIMEOUT event."
        ));
        
        report.setImpact(List.of(
                "Order creation failed.",
                "Payment could not be confirmed."
        ));
        
        report.setRecommendedInvestigation(List.of(
                "Inspect Payment Provider latency metrics.",
                "Review external provider availability.",
                "Check whether provider latency is affecting other requests."
        ));
        
        report.setAiAssessment("Agrees with deterministic analysis");

        return report;
    }
}
