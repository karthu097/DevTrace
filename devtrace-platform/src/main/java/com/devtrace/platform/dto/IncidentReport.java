package com.devtrace.platform.dto;

import java.util.List;

public class IncidentReport {
    private String traceId;
    private String severity;
    private String incidentSummary;
    private RootCauseCandidate rootCause;
    private List<String> whatHappened;
    private List<String> evidence;
    private List<String> impact;
    private List<String> recommendedInvestigation;
    private String aiAssessment;

    // Getters and Setters
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getIncidentSummary() { return incidentSummary; }
    public void setIncidentSummary(String incidentSummary) { this.incidentSummary = incidentSummary; }
    public RootCauseCandidate getRootCause() { return rootCause; }
    public void setRootCause(RootCauseCandidate rootCause) { this.rootCause = rootCause; }
    public List<String> getWhatHappened() { return whatHappened; }
    public void setWhatHappened(List<String> whatHappened) { this.whatHappened = whatHappened; }
    public List<String> getEvidence() { return evidence; }
    public void setEvidence(List<String> evidence) { this.evidence = evidence; }
    public List<String> getImpact() { return impact; }
    public void setImpact(List<String> impact) { this.impact = impact; }
    public List<String> getRecommendedInvestigation() { return recommendedInvestigation; }
    public void setRecommendedInvestigation(List<String> recommendedInvestigation) { this.recommendedInvestigation = recommendedInvestigation; }
    public String getAiAssessment() { return aiAssessment; }
    public void setAiAssessment(String aiAssessment) { this.aiAssessment = aiAssessment; }
}
