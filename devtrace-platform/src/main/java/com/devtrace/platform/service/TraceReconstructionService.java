package com.devtrace.platform.service;

import com.devtrace.platform.dto.ReconstructedTrace;
import com.devtrace.platform.entity.Span;
import com.devtrace.platform.entity.Trace;
import com.devtrace.platform.repository.SpanRepository;
import com.devtrace.platform.repository.TraceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraceReconstructionService {

    private final TraceRepository traceRepository;
    private final SpanRepository spanRepository;

    public TraceReconstructionService(TraceRepository traceRepository, SpanRepository spanRepository) {
        this.traceRepository = traceRepository;
        this.spanRepository = spanRepository;
    }

    public ReconstructedTrace reconstruct(String traceId) {
        Trace trace = traceRepository.findById(traceId).orElse(null);
        if (trace == null) {
            return null;
        }

        List<Span> spans = spanRepository.findByTraceIdOrderByStartTimeAsc(traceId);
        
        return new ReconstructedTrace(
                trace.getTraceId(),
                trace.getDurationMs(),
                trace.getStatus(),
                trace.getRootService(),
                spans
        );
    }
}
