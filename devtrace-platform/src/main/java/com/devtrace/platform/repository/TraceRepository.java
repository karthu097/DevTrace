package com.devtrace.platform.repository;

import com.devtrace.platform.entity.Trace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TraceRepository extends JpaRepository<Trace, String> {
}
