# AGENTS.md

## Best‑Practice Requirements for Java Implementation Agents

### 1. General Coding Standards
- Use Java 17+ for long-term support and improved performance features.
- Follow consistent coding standards (Google Java Style or equivalent).
- Enforce static analysis: SpotBugs, PMD, Checkstyle.
- Use SOLID principles and apply appropriate design patterns (Builder, Factory, Strategy, Hexagonal Architecture).

### 2. Recommended Libraries and Frameworks
- Use Spring Boot 3.x or similar modern framework for REST services.
- Prefer WebFlux for reactive pipelines if high concurrency/low-latency is required.
- Use Jackson with strict configuration for JSON parsing.
- Utilize Java Concurrency utilities (`java.util.concurrent`, `CompletableFuture`, etc.).
- Integrate OpenTelemetry for tracing and Micrometer for metrics.

### 3. Performance & Optimization
- Favor immutable objects; use Java Records where appropriate.
- Avoid excessive logging in hot paths.
- Reuse heavy objects (e.g., ObjectMapper).
- Tune thread pools to workload characteristics.
- Avoid unnecessary object allocations and enable compiler optimizations.

### 4. Security Best Practices
- Make TLS optional but configurable (cert path, key path, protocols).
- Block insecure serialization features; validate input strictly.
- Redact PII from logs (Supi, Gpsi, Imei).
- Implement payload limits and audit header validation.
- Avoid logging sensitive configuration values.

### 5. Concurrency & Synchronization
- Use per-session locking; avoid global locks.
- Use ConcurrentHashMap or other lock-efficient structures.
- Favor atomic operations and CAS patterns when possible.
- Ensure session mutations follow strict ordering rules.
- Avoid deadlocks by using well-defined lock ordering.

### 6. Memory Management
- Monitor GC; prefer G1 or ZGC for latency-sensitive workloads.
- Avoid unnecessary object churn to reduce GC pressure.
- Use defensive copying carefully and avoid deep copies unless needed.

### 7. Architectural Guidelines
- Separate API, domain logic, and infrastructure layers.
- Use hexagonal architecture for better testability.
- Keep OpenAPI-generated models isolated from domain entities.
- Maintain high cohesion and low coupling between components.

### 8. Serialization/Deserialization Rules
- Enforce strict JSON schema compliance and type safety.
- Validate RFC 3339 timestamps and numeric boundaries.
- Avoid re-parsing of the same payload multiple times.

### 9. Testing Strategy
- Use JUnit 5 for unit tests.
- Mock external dependencies using Mockito or WireMock.
- Provide integration tests (booting CHF module in-memory).
- Provide contract tests validating against OpenAPI schema.
- Provide performance/load tests using Gatling/JMeter.

### 10. Packaging & Deployment
- Generate deterministic builds using Maven or Gradle.
- Build minimal container images (Distroless or Alpine base).
- Version artifact metadata with git commit, build timestamp.
- Use jlink/jpackage to trim the JRE footprint if required.

### 11. Professional Logging Guidelines
- Always log as structured JSON.
- Use correlation IDs and include them in all logs.
- Use DEBUG logs only for redacted payloads.
- Apply field masking rules consistently.

### 12. Metrics Implementation
- Expose Prometheus-compatible metrics.
- Maintain counters for Create/Update/Release operations.
- Track idempotency hits, session counts, failures.
- Use histograms for latency measurement.

### 13. Tracing Implementation
- Use OpenTelemetry with W3C trace context propagation.
- Create spans for key phases: parsing, validation, session access, response generation.
- Include relevant span attributes (sessionId, sequenceNumber, state).
- Support configurable sampling rate.

### 14. Configuration Management
- Externalize all config: environment variables, YAML/JSON, system properties.
- Validate configs at startup; fail fast on invalid values.
- Redact sensitive config entries in logs.
- Support optional live reload where safe.

### 15. Resilience & Robustness
- Set timeouts on all I/O operations.
- Avoid retries for state-changing operations.
- Provide health probes (/health, /ready).
- Implement safe shutdown semantics.

### 16. Documentation
- Auto-generate API docs from OpenAPI.
- Use Javadoc consistently.
- Maintain developer guides for architecture and configuration.
