package com.devtrace.platform.dto;

public class FailureChainEvent {
    private String service;
    private String event;

    public FailureChainEvent(String service, String event) {
        this.service = service;
        this.event = event;
    }

    public String getService() { return service; }
    public String getEvent() { return event; }
}
