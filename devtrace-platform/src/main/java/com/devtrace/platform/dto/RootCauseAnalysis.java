package com.devtrace.platform.dto;

import java.util.List;
import java.util.Map;

public class RootCauseAnalysis {
    private String traceId;
    private String overallStatus;
    private RootCauseCandidate rootCause;
    private Double confidence;
    private List<Evidence> evidence;
    private List<String> affectedServices;
    private List<FailureChainEvent> failureChain;
    private List<String> criticalPath;

    // Getters and Setters
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getOverallStatus() { return overallStatus; }
    public void setOverallStatus(String overallStatus) { this.overallStatus = overallStatus; }
    public RootCauseCandidate getRootCause() { return rootCause; }
    public void setRootCause(RootCauseCandidate rootCause) { this.rootCause = rootCause; }
    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }
    public List<Evidence> getEvidence() { return evidence; }
    public void setEvidence(List<Evidence> evidence) { this.evidence = evidence; }
    public List<String> getAffectedServices() { return affectedServices; }
    public void setAffectedServices(List<String> affectedServices) { this.affectedServices = affectedServices; }
    public List<FailureChainEvent> getFailureChain() { return failureChain; }
    public void setFailureChain(List<FailureChainEvent> failureChain) { this.failureChain = failureChain; }
    public List<String> getCriticalPath() { return criticalPath; }
    public void setCriticalPath(List<String> criticalPath) { this.criticalPath = criticalPath; }
}
