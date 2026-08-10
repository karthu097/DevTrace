package com.devtrace.platform.service;

import com.devtrace.platform.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AIInvestigationServiceTest {

    private AIProvider mockProvider;
    private TraceAnalysisService mockAnalysisService;
    private TelemetrySanitizationService sanitizationService;
    private InvestigationCacheService cacheService;
    private AIInvestigationService aiInvestigationService;

    @BeforeEach
    void setUp() {
        mockProvider = mock(AIProvider.class);
        mockAnalysisService = mock(TraceAnalysisService.class);
        sanitizationService = new TelemetrySanitizationService();
        cacheService = new InvestigationCacheService();
        aiInvestigationService = new AIInvestigationService(mockProvider, mockAnalysisService, sanitizationService, cacheService);
    }

    @Test
    void testInvestigationFallbackWhenProviderFails() {
        ReconstructedTrace trace = new ReconstructedTrace("trace-123", 1000L, "ERROR", "svc", null);
        
        RootCauseAnalysis rca = new RootCauseAnalysis();
        rca.setRootCause(new RootCauseCandidate("svc", "dep", "TIMEOUT", "Timeout", 50));
        when(mockAnalysisService.analyzeTrace(trace)).thenReturn(rca);
        
        when(mockProvider.generateInvestigationReport(any())).thenThrow(new RuntimeException("API Down"));

        IncidentReport report = aiInvestigationService.investigate(trace);

        assertEquals("trace-123", report.getTraceId());
        assertEquals("AI unavailable. Deterministic fallback provided.", report.getIncidentSummary());
        assertNotNull(report.getRootCause());
        assertEquals("TIMEOUT", report.getRootCause().getType());
    }

    @Test
    void testCacheIsUsed() {
        ReconstructedTrace trace = new ReconstructedTrace("trace-123", 1000L, "ERROR", "svc", null);
        
        IncidentReport cachedReport = new IncidentReport();
        cachedReport.setTraceId("trace-123");
        cachedReport.setIncidentSummary("Cached result");
        cacheService.cacheReport("trace-123", cachedReport);

        IncidentReport result = aiInvestigationService.investigate(trace);
        
        assertEquals("Cached result", result.getIncidentSummary());
        // Verify provider was never called
        verify(mockProvider, never()).generateInvestigationReport(any());
        // Verify deterministic analysis was skipped
        verify(mockAnalysisService, never()).analyzeTrace(any());
    }
    
    @Test
    void testTelemetrySanitizationMasksSecrets() {
        InvestigationContext context = new InvestigationContext();
        context.setEvidence(List.of(
            new Evidence("INFO", "span1", "svc", "Attempting login with password='mySecretPassword123'", Instant.now())
        ));
        
        InvestigationContext sanitized = sanitizationService.sanitize(context);
        
        String maskedMsg = sanitized.getEvidence().get(0).getMessage();
        assertTrue(maskedMsg.contains("password=[REDACTED]"));
        assertFalse(maskedMsg.contains("mySecretPassword123"));
    }
}
