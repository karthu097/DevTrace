package com.devtrace.platform.dto;

import com.devtrace.platform.entity.Span;
import java.util.List;

public class ReconstructedTrace {
    private String traceId;
    private Long durationMs;
    private String status;
    private String rootService;
    private List<Span> spans;

    public ReconstructedTrace(String traceId, Long durationMs, String status, String rootService, List<Span> spans) {
        this.traceId = traceId;
        this.durationMs = durationMs;
        this.status = status;
        this.rootService = rootService;
        this.spans = spans;
    }

    public String getTraceId() { return traceId; }
    public Long getDurationMs() { return durationMs; }
    public String getStatus() { return status; }
    public String getRootService() { return rootService; }
    public List<Span> getSpans() { return spans; }
}
