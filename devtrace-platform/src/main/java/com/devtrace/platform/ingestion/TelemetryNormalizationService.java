package com.devtrace.platform.ingestion;

import com.devtrace.platform.entity.Span;
import com.devtrace.platform.entity.Trace;
import com.devtrace.platform.repository.SpanRepository;
import com.devtrace.platform.repository.TraceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TelemetryNormalizationService {

    private static final Logger log = LoggerFactory.getLogger(TelemetryNormalizationService.class);

    private final TraceRepository traceRepository;
    private final SpanRepository spanRepository;

    public TelemetryNormalizationService(TraceRepository traceRepository, SpanRepository spanRepository) {
        this.traceRepository = traceRepository;
        this.spanRepository = spanRepository;
    }

    @Transactional
    public void ingestOtlpJson(JsonNode root) {
        if (!root.has("resourceSpans")) return;

        for (JsonNode resourceSpan : root.get("resourceSpans")) {
            String serviceName = extractServiceName(resourceSpan.path("resource").path("attributes"));

            for (JsonNode scopeSpan : resourceSpan.path("scopeSpans")) {
                for (JsonNode spanNode : scopeSpan.path("spans")) {
                    processSpan(spanNode, serviceName);
                }
            }
        }
    }

    private void processSpan(JsonNode spanNode, String serviceName) {
        String traceId = spanNode.path("traceId").asText();
        String spanId = spanNode.path("spanId").asText();
        
        // Idempotency check
        if (spanRepository.existsById(spanId)) {
            return;
        }

        Span span = new Span();
        span.setSpanId(spanId);
        span.setTraceId(traceId);
        span.setParentSpanId(spanNode.hasNonNull("parentSpanId") ? spanNode.path("parentSpanId").asText() : null);
        span.setServiceName(serviceName);
        span.setOperationName(spanNode.path("name").asText());

        long startNanos = spanNode.path("startTimeUnixNano").asLong(0);
        long endNanos = spanNode.path("endTimeUnixNano").asLong(0);
        span.setStartTime(Instant.ofEpochSecond(startNanos / 1_000_000_000, startNanos % 1_000_000_000));
        span.setEndTime(Instant.ofEpochSecond(endNanos / 1_000_000_000, endNanos % 1_000_000_000));
        span.setDurationMs((endNanos - startNanos) / 1_000_000);

        span.setKind(spanNode.path("kind").asInt());

        JsonNode statusNode = spanNode.path("status");
        int statusCode = statusNode.path("code").asInt(0);
        span.setStatus(statusCode == 2 ? "ERROR" : "OK"); // OTLP status: 0=UNSET, 1=OK, 2=ERROR
        span.setStatusMessage(statusNode.path("message").asText(null));

        Map<String, Object> attrs = parseAttributes(spanNode.path("attributes"));
        span.setAttributes(attrs);
        // Events are ignored for brevity in parsing, but can be added if needed

        spanRepository.save(span);
        updateTrace(span);
    }

    private void updateTrace(Span span) {
        Trace trace = traceRepository.findById(span.getTraceId()).orElseGet(() -> {
            Trace t = new Trace();
            t.setTraceId(span.getTraceId());
            return t;
        });

        // Update trace bounds
        if (trace.getStartTime() == null || span.getStartTime().isBefore(trace.getStartTime())) {
            trace.setStartTime(span.getStartTime());
            if (span.getParentSpanId() == null || span.getParentSpanId().isEmpty()) {
                trace.setRootService(span.getServiceName());
                trace.setRootOperation(span.getOperationName());
            }
        }
        if (trace.getEndTime() == null || span.getEndTime().isAfter(trace.getEndTime())) {
            trace.setEndTime(span.getEndTime());
        }

        if (trace.getStartTime() != null && trace.getEndTime() != null) {
            trace.setDurationMs(trace.getEndTime().toEpochMilli() - trace.getStartTime().toEpochMilli());
        }

        if ("ERROR".equals(span.getStatus())) {
            trace.setStatus("ERROR");
        } else if (trace.getStatus() == null) {
            trace.setStatus("OK");
        }

        traceRepository.save(trace);
    }

    private String extractServiceName(JsonNode attributesNode) {
        if (attributesNode.isArray()) {
            for (JsonNode attr : attributesNode) {
                if ("service.name".equals(attr.path("key").asText())) {
                    return attr.path("value").path("stringValue").asText("unknown");
                }
            }
        }
        return "unknown";
    }

    private Map<String, Object> parseAttributes(JsonNode attributesNode) {
        Map<String, Object> map = new HashMap<>();
        if (attributesNode.isArray()) {
            for (JsonNode attr : attributesNode) {
                String key = attr.path("key").asText();
                JsonNode valueNode = attr.path("value");
                if (valueNode.has("stringValue")) {
                    map.put(key, valueNode.path("stringValue").asText());
                } else if (valueNode.has("intValue")) {
                    map.put(key, valueNode.path("intValue").asLong());
                } else if (valueNode.has("boolValue")) {
                    map.put(key, valueNode.path("boolValue").asBoolean());
                } else if (valueNode.has("doubleValue")) {
                    map.put(key, valueNode.path("doubleValue").asDouble());
                }
            }
        }
        return map;
    }
}
