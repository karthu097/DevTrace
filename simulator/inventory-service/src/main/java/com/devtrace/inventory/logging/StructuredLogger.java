package com.devtrace.inventory.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StructuredLogger {

    private static final Logger log = LoggerFactory.getLogger(StructuredLogger.class);

    public void info(String event, String requestId, String message) {
        log.info("event={} requestId={} message={}", event, requestId, message);
    }

    public void error(String event, String requestId, String message) {
        log.error("event={} requestId={} message={}", event, requestId, message);
    }
}
