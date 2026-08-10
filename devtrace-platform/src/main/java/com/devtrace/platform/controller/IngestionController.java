package com.devtrace.platform.controller;

import com.devtrace.platform.ingestion.TelemetryNormalizationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingest/v1")
public class IngestionController {

    private static final Logger log = LoggerFactory.getLogger(IngestionController.class);
    private final TelemetryNormalizationService normalizationService;

    public IngestionController(TelemetryNormalizationService normalizationService) {
        this.normalizationService = normalizationService;
    }

    @PostMapping("/traces")
    public ResponseEntity<Void> ingestTraces(@RequestBody JsonNode payload) {
        try {
            normalizationService.ingestOtlpJson(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to ingest traces", e);
            return ResponseEntity.internalServerError().build(); // Or OK to prevent retries of bad data
        }
    }
}
