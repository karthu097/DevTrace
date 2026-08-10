# Architecture

DevTrace is broken into three main macro-components: the **Observability Pipeline**, the **DevTrace Engine (Backend)**, and the **Developer UI (Frontend)**.

## 1. Observability Pipeline
Microservices (in this case, our Java Simulator) are instrumented with the OpenTelemetry Java Agent. As traffic flows through the system, the agent generates Spans.

These Spans are sent via OTLP (gRPC/HTTP) to an **OpenTelemetry Collector**. The collector batches and forwards these traces simultaneously to Jaeger (for raw storage) and to the DevTrace Engine API.

## 2. DevTrace Engine (`devtrace-platform`)
Built with Java 21 and Spring Boot, this is the brain of the platform.

### Ingestion
The `/api/ingest/spans` endpoint receives raw JSON from the OTEL collector and persists it into PostgreSQL using `jsonb` column types for flexibility.

### Trace Reconstruction
Because spans arrive out-of-order, the `TraceReconstructionService` retrieves all spans sharing a `traceId` and reconstructs the parent-child hierarchy in memory.

### Deterministic Root-Cause Engine
The `TraceAnalysisService` walks the reconstructed tree. It identifies the "critical path" (the longest blocking chain of spans) and searches for the deepest node in that path that threw an error. That node is algorithmically marked as the root cause.

### AI Investigation Layer
The engine collects all error messages, HTTP statuses, and logs from the trace. The `TelemetrySanitizationService` scrubs passwords, credit cards, and tokens. The scrubbed evidence is sent to OpenAI to generate an Incident Report.

## 3. Developer UI (`devtrace-ui`)
A React SPA built with Vite. It consumes the Engine's REST APIs. It renders:
- A high-level Dashboard for system health.
- `React Flow` to draw real-time dependency topology.
- A custom-built Trace Waterfall visualization mapping proportional span durations.
