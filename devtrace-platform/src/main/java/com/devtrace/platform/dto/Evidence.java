package com.devtrace.platform.dto;

import java.time.Instant;

public class Evidence {
    private String type;
    private String sourceSpan;
    private String service;
    private String message;
    private Instant timestamp;

    public Evidence(String type, String sourceSpan, String service, String message, Instant timestamp) {
        this.type = type;
        this.sourceSpan = sourceSpan;
        this.service = service;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public String getSourceSpan() { return sourceSpan; }
    public String getService() { return service; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }
}
