package com.devtrace.platform.repository;

import com.devtrace.platform.entity.Span;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpanRepository extends JpaRepository<Span, String> {
    List<Span> findByTraceId(String traceId);
    List<Span> findByTraceIdOrderByStartTimeAsc(String traceId);
}
