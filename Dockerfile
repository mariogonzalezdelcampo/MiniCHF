# Multi-stage Dockerfile for Spring Boot application

# Stage 1: Build
FROM maven:3.8.1-openjdk-17-slim as builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-slim
WORKDIR /app

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy application from builder
COPY --from=builder /build/target/nchf-converged-charging-1.0.0.jar application.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Default port
EXPOSE 8080

# Metadata
LABEL service.version="1.0.0" \
      service.name="nchf-converged-charging" \
      description="MiniCHF - Minimal Converged Charging Function for 5G"

# Environment variables (can be overridden at runtime)
ENV SERVER_PORT=8080 \
    SERVER_TLS_ENABLED=false \
    METRICS_ENABLED=true \
    LOGGING_LEVEL=INFO \
    SPRING_PROFILES_ACTIVE=prod

# Entrypoint
ENTRYPOINT ["java", "-jar", "application.jar"]
