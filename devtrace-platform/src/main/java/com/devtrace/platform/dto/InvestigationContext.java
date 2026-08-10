package com.devtrace.platform.dto;

import java.util.List;

public class InvestigationContext {
    private String traceId;
    private String status;
    private Long durationMs;
    private RootCauseCandidate deterministicRootCause;
    private List<String> criticalPath;
    private List<String> failedServices;
    private List<Evidence> evidence;

    // Getters and Setters
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public RootCauseCandidate getDeterministicRootCause() { return deterministicRootCause; }
    public void setDeterministicRootCause(RootCauseCandidate deterministicRootCause) { this.deterministicRootCause = deterministicRootCause; }
    public List<String> getCriticalPath() { return criticalPath; }
    public void setCriticalPath(List<String> criticalPath) { this.criticalPath = criticalPath; }
    public List<String> getFailedServices() { return failedServices; }
    public void setFailedServices(List<String> failedServices) { this.failedServices = failedServices; }
    public List<Evidence> getEvidence() { return evidence; }
    public void setEvidence(List<Evidence> evidence) { this.evidence = evidence; }
}
