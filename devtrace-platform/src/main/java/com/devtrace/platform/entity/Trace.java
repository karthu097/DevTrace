package com.devtrace.platform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "traces")
public class Trace {

    @Id
    private String traceId;

    private Instant startTime;
    private Instant endTime;
    private Long durationMs;
    private String status;
    private String rootService;
    private String rootOperation;

    // Getters and Setters
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRootService() { return rootService; }
    public void setRootService(String rootService) { this.rootService = rootService; }
    public String getRootOperation() { return rootOperation; }
    public void setRootOperation(String rootOperation) { this.rootOperation = rootOperation; }
}
