package com.devtrace.platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/demo/incidents")
public class DemoIncidentController {

    private static final Logger log = LoggerFactory.getLogger(DemoIncidentController.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final String GATEWAY_URL = "http://api-gateway:8080/checkout"; // Assuming docker network

    @PostMapping("/{type}")
    public ResponseEntity<String> triggerIncident(@PathVariable String type) {
        log.info("Triggering demo incident: {}", type);
        try {
            // This assumes the simulator accepts headers or query params to force failures.
            // For a portfolio demo, we hit the simulator to generate a failed trace.
            String url = GATEWAY_URL + "?force_error=" + type;
            restTemplate.getForObject(url, String.class);
            return ResponseEntity.ok("Incident triggered: " + type);
        } catch (Exception e) {
            log.warn("Failed to reach simulator (this is normal if testing locally without docker): {}", e.getMessage());
            return ResponseEntity.ok("Trigger request sent (with errors): " + type);
        }
    }
}
