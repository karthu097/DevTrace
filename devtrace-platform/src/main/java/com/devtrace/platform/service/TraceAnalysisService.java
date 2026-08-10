package com.devtrace.platform.service;

import com.devtrace.platform.dto.*;
import com.devtrace.platform.entity.Span;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TraceAnalysisService {

    @Value("${devtrace.analysis.slow-span-threshold-ms:1000}")
    private long slowSpanThresholdMs;

    public RootCauseAnalysis analyzeTrace(ReconstructedTrace trace) {
        RootCauseAnalysis analysis = new RootCauseAnalysis();
        analysis.setTraceId(trace.getTraceId());
        analysis.setOverallStatus(trace.getStatus());

        if (trace.getSpans() == null || trace.getSpans().isEmpty()) {
            return analysis;
        }

        List<Evidence> evidenceList = new ArrayList<>();
        List<RootCauseCandidate> candidates = new ArrayList<>();
        Set<String> affectedServices = new HashSet<>();
        List<FailureChainEvent> failureChain = new ArrayList<>();

        Span longestSpan = null;

        // Simple single-pass evaluation
        for (Span span : trace.getSpans()) {
            if (span.getDurationMs() != null) {
                if (longestSpan == null || span.getDurationMs() > longestSpan.getDurationMs()) {
                    longestSpan = span;
                }
            }

            if ("ERROR".equals(span.getStatus())) {
                affectedServices.add(span.getServiceName());
                
                String errMsg = span.getStatusMessage() != null ? span.getStatusMessage() : "";
                
                // Rule 1: Timeout
                if (errMsg.toLowerCase().contains("timeout") || hasTimeoutEvent(span)) {
                    candidates.add(new RootCauseCandidate(
                            span.getServiceName(),
                            extractDependency(span),
                            "TIMEOUT",
                            span.getServiceName() + " timed out calling dependency.",
                            40
                    ));
                    evidenceList.add(new Evidence("TIMEOUT", span.getSpanId(), span.getServiceName(), "Timeout detected: " + errMsg, span.getStartTime()));
                    failureChain.add(new FailureChainEvent(span.getServiceName(), "TIMEOUT"));
                } 
                // Rule 2: HTTP 5xx
                else if (isHttp5xx(span)) {
                    candidates.add(new RootCauseCandidate(
                            span.getServiceName(),
                            extractDependency(span),
                            "DEPENDENCY_FAILURE",
                            "Dependency returned HTTP 5xx",
                            35
                    ));
                    evidenceList.add(new Evidence("HTTP_5XX", span.getSpanId(), span.getServiceName(), "HTTP 5xx status code", span.getStartTime()));
                    failureChain.add(new FailureChainEvent(span.getServiceName(), "HTTP_5XX"));
                }
                // Rule 3: Exception
                else if (errMsg.toLowerCase().contains("exception")) {
                    candidates.add(new RootCauseCandidate(
                            span.getServiceName(),
                            null,
                            "APPLICATION_EXCEPTION",
                            "Application exception thrown",
                            15
                    ));
                    evidenceList.add(new Evidence("EXCEPTION", span.getSpanId(), span.getServiceName(), errMsg, span.getStartTime()));
                    failureChain.add(new FailureChainEvent(span.getServiceName(), "EXCEPTION"));
                } else {
                    failureChain.add(new FailureChainEvent(span.getServiceName(), "ERROR"));
                }
            }
        }

        // Rule 5: High Latency
        if (longestSpan != null && longestSpan.getDurationMs() > slowSpanThresholdMs) {
            candidates.add(new RootCauseCandidate(
                    longestSpan.getServiceName(),
                    extractDependency(longestSpan),
                    "RESOURCE_LATENCY",
                    "Span exceeded latency threshold: " + longestSpan.getDurationMs() + "ms",
                    20
            ));
            evidenceList.add(new Evidence("HIGH_LATENCY", longestSpan.getSpanId(), longestSpan.getServiceName(), "Duration: " + longestSpan.getDurationMs() + "ms", longestSpan.getStartTime()));
        }

        // Pick best candidate
        RootCauseCandidate bestCandidate = candidates.stream()
                .max(Comparator.comparingInt(RootCauseCandidate::getScore))
                .orElse(new RootCauseCandidate("unknown", null, "UNKNOWN", "DevTrace could not determine a reliable root cause", 0));

        analysis.setRootCause(bestCandidate);
        analysis.setConfidence(bestCandidate.getScore() > 0 ? Math.min(1.0, bestCandidate.getScore() / 100.0) : 0.31);
        analysis.setEvidence(evidenceList);
        analysis.setAffectedServices(new ArrayList<>(affectedServices));
        analysis.setFailureChain(failureChain);

        // Compute Critical Path (simplified: just path to longest span or error span)
        List<String> criticalPath = new ArrayList<>();
        Span current = longestSpan != null ? longestSpan : trace.getSpans().get(trace.getSpans().size() - 1);
        while (current != null) {
            criticalPath.add(0, current.getServiceName()); // Prepend
            String parentId = current.getParentSpanId();
            current = trace.getSpans().stream().filter(s -> s.getSpanId().equals(parentId)).findFirst().orElse(null);
        }
        
        // Remove consecutive duplicates
        List<String> uniqueCriticalPath = new ArrayList<>();
        String last = null;
        for (String s : criticalPath) {
            if (!s.equals(last)) {
                uniqueCriticalPath.add(s);
                last = s;
            }
        }
        analysis.setCriticalPath(uniqueCriticalPath);

        return analysis;
    }

    private boolean hasTimeoutEvent(Span span) {
        if (span.getEvents() == null) return false;
        return span.getEvents().stream().anyMatch(e -> {
            Object name = e.get("name");
            return name != null && name.toString().toLowerCase().contains("timeout");
        });
    }

    private boolean isHttp5xx(Span span) {
        if (span.getAttributes() == null) return false;
        Object status = span.getAttributes().get("http.status_code");
        if (status instanceof Integer) {
            int code = (Integer) status;
            return code >= 500 && code < 600;
        }
        return false;
    }

    private String extractDependency(Span span) {
        if (span.getAttributes() != null) {
            Object peer = span.getAttributes().get("peer.service");
            if (peer != null) return peer.toString();
            Object host = span.getAttributes().get("http.host");
            if (host != null) return host.toString();
        }
        return "downstream-dependency";
    }
}
