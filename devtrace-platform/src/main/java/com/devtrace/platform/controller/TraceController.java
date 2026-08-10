package com.devtrace.platform.controller;

import com.devtrace.platform.dto.ReconstructedTrace;
import com.devtrace.platform.entity.Trace;
import com.devtrace.platform.repository.TraceRepository;
import com.devtrace.platform.service.TraceReconstructionService;
import com.devtrace.platform.service.TraceAnalysisService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/traces")
@Validated
public class TraceController {

    private final TraceRepository traceRepository;
    private final TraceReconstructionService traceReconstructionService;
    private final TraceAnalysisService traceAnalysisService;
    private final com.devtrace.platform.service.AIInvestigationService aiInvestigationService;

    public TraceController(TraceRepository traceRepository, TraceReconstructionService traceReconstructionService, TraceAnalysisService traceAnalysisService, com.devtrace.platform.service.AIInvestigationService aiInvestigationService) {
        this.traceRepository = traceRepository;
        this.traceReconstructionService = traceReconstructionService;
        this.traceAnalysisService = traceAnalysisService;
        this.aiInvestigationService = aiInvestigationService;
    }

    @GetMapping
    public ResponseEntity<Page<Trace>> getTraces(Pageable pageable) {
        // Simplified search for now
        return ResponseEntity.ok(traceRepository.findAll(pageable));
    }

    @GetMapping("/{traceId}")
    public ResponseEntity<ReconstructedTrace> getTrace(@PathVariable @Size(min = 1, max = 255) String traceId) {
        ReconstructedTrace trace = traceReconstructionService.reconstruct(traceId);
        if (trace == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(trace);
    }

    @GetMapping("/{traceId}/root-cause")
    public ResponseEntity<com.devtrace.platform.dto.RootCauseAnalysis> getRootCause(@PathVariable @Size(min = 1, max = 255) String traceId) {
        ReconstructedTrace trace = traceReconstructionService.reconstruct(traceId);
        if (trace == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(traceAnalysisService.analyzeTrace(trace));
    }

    @GetMapping("/{traceId}/investigation")
    public ResponseEntity<com.devtrace.platform.dto.IncidentReport> getInvestigation(@PathVariable @Size(min = 1, max = 255) String traceId) {
        ReconstructedTrace trace = traceReconstructionService.reconstruct(traceId);
        if (trace == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(aiInvestigationService.investigate(trace));
    }
}
