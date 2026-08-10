package com.devtrace.platform.service;

import com.devtrace.platform.dto.IncidentReport;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InvestigationCacheService {

    private final ConcurrentHashMap<String, IncidentReport> cache = new ConcurrentHashMap<>();

    public IncidentReport getCachedReport(String traceId) {
        return cache.get(traceId);
    }

    public void cacheReport(String traceId, IncidentReport report) {
        if (traceId != null && report != null) {
            cache.put(traceId, report);
        }
    }
}
