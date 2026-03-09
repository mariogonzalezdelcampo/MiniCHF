# MiniCHF - Converged Charging Function (Phase 1)

## Overview

This is a minimal HTTP/2 REST server implementation for the Nchf-ConvergedCharging API following the OpenAPI specifications provided.

### Phase 1 Implementation Status

The following Phase 1 requirements have been implemented:

✅ HTTP/2 REST endpoint scaffold with configurable port (default 8080)
✅ Cleartext HTTP/2 (h2c) support for local development
✅ Optional HTTPS/TLS support (disabled by default, configurable)
✅ JSON payload processing with UTF-8 encoding
✅ Liveness endpoint: `GET /health` returning 200 OK
✅ Readiness endpoint: `GET /ready` returning 200 OK
✅ Structured access logging with correlation/request IDs
✅ Content-Type and Accept header validation (415/406 responses)
✅ ProblemDetails error handler (application/problem+json)
✅ RFC 3339 date-time formatting for all timestamps
✅ Base routing for Create, Update, Release operations (stub implementations)
✅ Configurable maximum request size limit (default 1 MiB)
✅ Correlation/request ID generation and propagation
✅ Optional metrics endpoint at `/metrics` (disabled by default)
✅ Graceful shutdown with configurable timeout
✅ Comprehensive unit tests

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Server**: Netty with HTTP/2 support
- **Build Tool**: Maven 3.8+
- **Java Version**: Java 17 (LTS)
- **Reactive**: Spring WebFlux
- **Logging**: SLF4J + Logback
- **Metrics**: Micrometer + Prometheus

## Building the Project

```bash
# Build the project
mvn clean package

# Build without running tests
mvn clean package -DskipTests

# Run tests only
mvn test
```

## Running the Service

### From Maven
```bash
mvn spring-boot:run
```

### From JAR
```bash
java -jar target/nchf-converged-charging-1.0.0.jar
```

### With Environment Variables
```bash
# Configure port
SERVER_PORT=9090 java -jar target/nchf-converged-charging-1.0.0.jar

# Enable TLS
SERVER_TLS_ENABLED=true SERVER_TLS_CERT_PATH=/path/to/cert.pem SERVER_TLS_KEY_PATH=/path/to/key.pem java -jar target/nchf-converged-charging-1.0.0.jar

# Configure metrics
METRICS_ENABLED=true java -jar target/nchf-converged-charging-1.0.0.jar
```

## Configuration

### Environment Variables

- `SERVER_PORT`: Server listening port (default: 8080)
- `SERVER_TLS_ENABLED`: Enable HTTPS/TLS (default: false)
- `SERVER_TLS_CERT_PATH`: Path to TLS certificate
- `SERVER_TLS_KEY_PATH`: Path to TLS private key
- `SERVER_TLS_KEYSTORE_PATH`: Path to keystore file
- `SERVER_TLS_KEYSTORE_PASSWORD`: Keystore password
- `MAX_REQUEST_SIZE`: Maximum request body size in bytes (default: 1048576)
- `METRICS_ENABLED`: Enable metrics endpoint (default: true)
- `LOGGING_LEVEL`: Logging level (default: INFO)
- `LOGGING_FORMAT`: Log format - json or text (default: json)
- `SESSION_TTL_SECONDS`: Session time-to-live in seconds (default: -1, disabled)
- `QUOTA_DEFAULT_TIME`: Default time quota in seconds (default: 3600)
- `QUOTA_DEFAULT_VOLUME_TOTAL`: Default total volume quota in bytes
- `CDR_OUTPUT_DIR`: CDR output directory (default: ./cdr/)

### Application Configuration

All configuration can also be set in `application.yml` or environment-specific files:
- `application-dev.yml` - Development profile
- `application-prod.yml` - Production profile
- `application-test.yml` - Test profile

## API Endpoints

### Health & Readiness

- `GET /health` - Liveness probe (always returns 200 OK)
- `GET /ready` - Readiness probe (returns 200 OK when fully initialized)

### Metrics

- `GET /metrics` - Prometheus-style metrics (if enabled)
- `GET /health/live` - Kubernetes liveness probe
- `GET /health/ready` - Kubernetes readiness probe

### Charging Operations (Stub Implementations - Phase 1)

- `POST /nchf-convergedcharging/v2/chargingdata` - Create charging data (501 Not Implemented)
- `POST /nchf-convergedcharging/v2/chargingdata/{ChargingDataRef}/update` - Update charging data (501 Not Implemented)
- `POST /nchf-convergedcharging/v2/chargingdata/{ChargingDataRef}/release` - Release charging data (501 Not Implemented)
- `OPTIONS /nchf-convergedcharging/v2/chargingdata` - List allowed methods
- `OPTIONS /nchf-convergedcharging/v2/chargingdata/{ChargingDataRef}/update` - List allowed methods
- `OPTIONS /nchf-convergedcharging/v2/chargingdata/{ChargingDataRef}/release` - List allowed methods

## Request/Response Format

All requests and responses use `application/json` content type with UTF-8 encoding.

### Error Response (ProblemDetails)

```json
{
  "type": "about:blank",
  "title": "Unsupported Media Type",
  "status": 415,
  "detail": "Content-Type must be application/json",
  "instance": "/nchf-convergedcharging/v2/chargingdata",
  "timestamp": "2024-01-15T10:30:45.123+01:00",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Health Response

```json
{
  "status": "UP",
  "timestamp": "2024-01-15T10:30:45.123+01:00"
}
```

## Logging

Logs are emitted to both console and file (default location: ./spring.log).

Each log entry includes:
- Timestamp (RFC 3339 format)
- Thread name
- Log level (TRACE, DEBUG, INFO, WARN, ERROR)
- Logger name
- Message

Example log entries include correlation IDs for request tracing:
```
2024-01-15T10:30:45.123+01:00 [http-nio-8080-exec-1] INFO  com.minichf.api.controller.HealthController - method=GET path=/health status=200 correlationId=550e8400-e29b-41d4-a716-446655440000
```

## Testing

Run the test suite with:

```bash
mvn test
```

Test coverage includes:
- Health/readiness endpoints
- Content-Type/Accept header validation
- Request body validation
- Correlation ID generation and propagation
- ProblemDetails format validation
- RFC 3339 timestamp formatting
- OPTIONS method handling
- UUID validation
- Utility functions

## Graceful Shutdown

The service supports graceful shutdown with a 30-second wait period for active requests to complete.

Send SIGTERM signal:
```bash
kill -TERM <pid>
```

Or via HTTP (if management endpoints are exposed):
```bash
POST http://localhost:8080/actuator/shutdown
```

## Next Phases

- **Phase 2**: Bind OpenAPI models and generate from specifications
- **Phase 3**: Implement Create endpoint with 501 Not Implemented
- **Phase 4**: Decode ChargingDataRequest and logging
- And more...

See PLAN.md for complete implementation roadmap.

## Development

### Quick Start for Development

```bash
# Run with development profile
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Run tests in watch mode  
mvn test -Dwatch=true
```

### IDE Setup

1. Import the Maven project into your IDE
2. Set JDK version to Java 17
3. Enable annotation processing (for Lombok annotations)
4. Run tests from IDE to verify setup

## Monitoring and Observability

### Metrics Endpoint

If metrics are enabled, access Prometheus-compatible metrics at:
```
http://localhost:8080/actuator/metrics
```

Available metrics:
- HTTP request counts and latencies
- JVM metrics (memory, GC, threads)
- Application-specific metrics (when implemented)

### Structured Logging

Logs are structured with correlation IDs for distributed tracing:
- Request correlation IDs are propagated across all related log entries
- Both generated and client-provided correlation IDs are supported
- Correlation IDs are included in ProblemDetails responses

## Performance Characteristics

- Non-blocking I/O with Netty and Reactor
- HTTP/2 multiplexing support
- Efficient connection pooling
- Graceful backpressure handling
- Request timeout: 60s (configurable)

## Security Considerations

- TLS support for encrypted communications (optional)
- Content-Type validation to prevent injection attacks
- Request size limits to prevent DOS attacks
- Structured error responses without sensitive information exposure
- No default exposure of sensitive endpoints in production

## License

[To be added]

## Support

For issues and questions, please refer to the project documentation and PLAN.md for implementation details.
