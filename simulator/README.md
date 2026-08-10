# DevTrace

Welcome to DevTrace.

## Architecture
The system consists of the following components:
- **api-gateway** (`:8080`): The entry point for clients.
- **order-service** (`:8081`): Orchestrates orders, calls inventory and payment.
- **inventory-service** (`:8082`): Manages stock reservations.
- **payment-service** (`:8083`): Internal payment workflow logic.
- **payment-provider** (`:8084`): Simulates an external payment gateway with failures.
- **postgres** (`:5432`): Persistent data storage for orders, inventory, and payments.

## Distributed Tracing

DevTrace Phase 3 introduces **real distributed tracing** powered by OpenTelemetry.

### What is OpenTelemetry?
OpenTelemetry is an observability framework providing standard traces, metrics, and logs. We use the OpenTelemetry Java Agent, which attaches to our Spring Boot services during container startup and automatically instruments HTTP requests, Database queries, and internal Spring beans.

### What is the Collector?
The OpenTelemetry Collector (`otel-collector`) runs as a central ingestion pipeline in our Docker Compose network. All our Java applications export their trace data to the Collector via the OTLP (OpenTelemetry Protocol) over HTTP on port 4318. The Collector then processes and forwards this data to Jaeger.

### What is Jaeger?
Jaeger is an open-source, end-to-end distributed tracing visualization system. It receives traces from our Collector and provides a powerful web UI (accessible at `http://localhost:16686`) to search, filter, and inspect spans across the entire request path.

### Trace Propagation
When a request enters the API Gateway, a new Trace ID is generated. As the request travels to the Order Service, and subsequently to the Inventory and Payment services, the OpenTelemetry Agent automatically injects W3C Trace Context headers (e.g., `traceparent`) into the HTTP requests. The downstream services read these headers, allowing Jaeger to stitch all spans together into a single unified trace spanning the 5 applications.

### How to run the tracing stack
Ensure you have Docker and Docker Compose installed.
1. Make sure JARs are built: `mvn clean package -DskipTests` (inside `simulator`)
2. Start the cluster: `docker compose up --build -d`

### Accessing Jaeger
Open your browser and navigate to: [http://localhost:16686](http://localhost:16686)

### How to test tracing and failure scenarios
1. **Happy Path:**
   - Send an order: 
     `curl -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d '{"userId":101,"productId":501,"quantity":1}'`
   - Open Jaeger, select `devtrace-api-gateway`, and find the trace to see the full path across all services.

2. **Timeout Scenario (Canonical Incident):**
   - Configure the Provider to timeout:
     `curl -X POST http://localhost:8084/provider/simulation/failure?mode=TIMEOUT`
   - Send an order. The Payment Service's strict 3-second timeout will fire before the Provider's 5-second delay.
   - Open Jaeger. You will see an `ERROR` span clearly showing the timeout occurring precisely at the boundary between `payment-service` and `payment-provider`, along with the semantic JSON error structure propagated upward.
