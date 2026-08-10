package com.devtrace.platform.service;

import com.devtrace.platform.dto.IncidentReport;
import com.devtrace.platform.dto.InvestigationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;
import java.util.List;

@Service
@ConditionalOnProperty(name = "devtrace.ai.provider", havingValue = "openai")
public class OpenAiProvider implements AIProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiProvider.class);
    
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;
    
    public OpenAiProvider(
            @Value("${devtrace.ai.base-url:https://api.openai.com}") String baseUrl,
            @Value("${devtrace.ai.api-key:}") String apiKey,
            @Value("${devtrace.ai.model:gpt-4o-mini}") String model,
            ObjectMapper objectMapper) {
        
        this.model = model;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    @Override
    public IncidentReport generateInvestigationReport(InvestigationContext context) {
        try {
            String contextJson = objectMapper.writeValueAsString(context);
            
            String systemPrompt = """
                You are a senior DevTrace incident investigator.
                1. Use only the supplied evidence.
                2. Never invent logs, metrics, or timestamps.
                3. Clearly separate facts from inference.
                4. Prefer deterministic root-cause candidates.
                5. If evidence is insufficient, say so.
                6. Explain failure propagation.
                7. Identify the most likely root cause.
                8. Suggest practical next investigation steps.
                Output exactly as a structured JSON matching IncidentReport schema.
                """;

            Map<String, Object> request = Map.of(
                "model", model,
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", "Investigate this context: " + contextJson)
                )
            );

            String responseBody = restClient.post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);
            
            // Parse response
            var rootNode = objectMapper.readTree(responseBody);
            String aiJson = rootNode.path("choices").get(0).path("message").path("content").asText();
            
            IncidentReport report = objectMapper.readValue(aiJson, IncidentReport.class);
            report.setTraceId(context.getTraceId());
            return report;
            
        } catch (Exception e) {
            log.error("OpenAI investigation failed", e);
            throw new RuntimeException("AI Provider Failed", e);
        }
    }
}
