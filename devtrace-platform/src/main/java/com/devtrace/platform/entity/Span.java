package com.devtrace.platform.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.Map;
import java.util.List;

@Entity
@Table(name = "spans", indexes = {
    @Index(name = "idx_span_trace_id", columnList = "traceId"),
    @Index(name = "idx_span_parent_id", columnList = "parentSpanId"),
    @Index(name = "idx_span_service", columnList = "serviceName")
})
public class Span {

    @Id
    private String spanId;
    private String traceId;
    private String parentSpanId;
    private String serviceName;
    private String operationName;
    private Instant startTime;
    private Instant endTime;
    private Long durationMs;
    private String status;
    private String statusMessage;
    private Integer kind;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> events;

    // Getters and Setters
    public String getSpanId() { return spanId; }
    public void setSpanId(String spanId) { this.spanId = spanId; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getParentSpanId() { return parentSpanId; }
    public void setParentSpanId(String parentSpanId) { this.parentSpanId = parentSpanId; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public String getOperationName() { return operationName; }
    public void setOperationName(String operationName) { this.operationName = operationName; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
    public Integer getKind() { return kind; }
    public void setKind(Integer kind) { this.kind = kind; }
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    public List<Map<String, Object>> getEvents() { return events; }
    public void setEvents(List<Map<String, Object>> events) { this.events = events; }
}
