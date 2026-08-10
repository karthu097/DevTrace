CREATE TABLE traces (
    trace_id VARCHAR(255) PRIMARY KEY,
    start_time TIMESTAMP WITH TIME ZONE,
    end_time TIMESTAMP WITH TIME ZONE,
    duration_ms BIGINT,
    status VARCHAR(50),
    root_service VARCHAR(255),
    root_operation VARCHAR(255)
);

CREATE TABLE spans (
    span_id VARCHAR(255) PRIMARY KEY,
    trace_id VARCHAR(255) NOT NULL,
    parent_span_id VARCHAR(255),
    service_name VARCHAR(255),
    operation_name VARCHAR(255),
    start_time TIMESTAMP WITH TIME ZONE,
    end_time TIMESTAMP WITH TIME ZONE,
    duration_ms BIGINT,
    status VARCHAR(50),
    status_message TEXT,
    kind INTEGER,
    attributes JSONB,
    events JSONB
);

CREATE INDEX idx_span_trace_id ON spans(trace_id);
CREATE INDEX idx_span_parent_id ON spans(parent_span_id);
CREATE INDEX idx_span_service ON spans(service_name);
