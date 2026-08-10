package com.devtrace.platform.service;

import com.devtrace.platform.dto.IncidentReport;
import com.devtrace.platform.dto.InvestigationContext;
import com.devtrace.platform.dto.ReconstructedTrace;
import com.devtrace.platform.dto.RootCauseAnalysis;
import org.springframework.stereotype.Service;

@Service
public class AIInvestigationService {

    private final AIProvider aiProvider;
    private final TraceAnalysisService traceAnalysisService;
    private final TelemetrySanitizationService sanitizationService;
    private final InvestigationCacheService cacheService;

    public AIInvestigationService(AIProvider aiProvider, TraceAnalysisService traceAnalysisService, TelemetrySanitizationService sanitizationService, InvestigationCacheService cacheService) {
        this.aiProvider = aiProvider;
        this.traceAnalysisService = traceAnalysisService;
        this.sanitizationService = sanitizationService;
        this.cacheService = cacheService;
    }

    public IncidentReport investigate(ReconstructedTrace trace) {
        // 1. Check cache first
        IncidentReport cached = cacheService.getCachedReport(trace.getTraceId());
        if (cached != null) {
            return cached;
        }

        // 2. Run deterministic analysis
        RootCauseAnalysis analysis = traceAnalysisService.analyzeTrace(trace);

        // Build Investigation Context
        InvestigationContext context = new InvestigationContext();
        context.setTraceId(trace.getTraceId());
        context.setStatus(trace.getStatus());
        context.setDurationMs(trace.getDurationMs());
        context.setDeterministicRootCause(analysis.getRootCause());
        context.setCriticalPath(analysis.getCriticalPath());
        context.setFailedServices(analysis.getAffectedServices());
        context.setEvidence(analysis.getEvidence());
        // 4. Sanitize Context (Privacy)
        context = sanitizationService.sanitize(context);

        // 5. Delegate to AI Provider and Cache
        try {
            IncidentReport report = aiProvider.generateInvestigationReport(context);
            cacheService.cacheReport(trace.getTraceId(), report);
            return report;
        } catch (Exception e) {
            IncidentReport fallback = new IncidentReport();
            fallback.setTraceId(trace.getTraceId());
            fallback.setIncidentSummary("AI unavailable. Deterministic fallback provided.");
            fallback.setAiAssessment("Temporarily unavailable");
            fallback.setRootCause(analysis.getRootCause());
            return fallback;
        }
    }
}
