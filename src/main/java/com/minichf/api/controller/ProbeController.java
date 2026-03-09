package com.minichf.api.controller;

import com.minichf.domain.model.HealthStatus;
import com.minichf.domain.model.ProblemDetails;
import com.minichf.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Liveness and readiness probe endpoints at root level
 */
@Slf4j
@RestController
@RequestMapping
public class ProbeController {

    private volatile boolean readyFlag = true;

    /**
     * Liveness probe: GET /health
     * Returns 200 OK with basic status
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<HealthStatus>> health(ServerWebExchange exchange) {
        String timestamp = CorrelationIdUtil.getCurrentTimestamp();
        String correlationId = extractCorrelationId(exchange.getRequest());
        
        logAccess(exchange, "/health", 200, correlationId);
        
        HealthStatus status = new HealthStatus("UP", timestamp);
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationIdUtil.CORRELATION_HEADER, correlationId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return Mono.just(ResponseEntity.ok()
                .headers(headers)
                .body(status));
    }

    /**
     * Readiness probe: GET /ready
     * Returns 200 OK only when initialization is complete
     */
    @GetMapping("/ready")
    public Mono<ResponseEntity<?>> ready(ServerWebExchange exchange) {
        String timestamp = CorrelationIdUtil.getCurrentTimestamp();
        String correlationId = extractCorrelationId(exchange.getRequest());
        
        if (!readyFlag) {
            logAccess(exchange, "/ready", 503, correlationId);
            ProblemDetails problem = new ProblemDetails(
                    503,
                    "Service Unavailable",
                    "Service is not ready for requests"
            );
            problem.setCorrelationId(correlationId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set(CorrelationIdUtil.CORRELATION_HEADER, correlationId);
            headers.setContentType(MediaType.valueOf("application/problem+json"));
            
            return Mono.just(ResponseEntity.status(503)
                    .headers(headers)
                    .body(problem));
        }

        logAccess(exchange, "/ready", 200, correlationId);
        
        HealthStatus status = new HealthStatus("READY", timestamp);
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationIdUtil.CORRELATION_HEADER, correlationId);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        return Mono.just(ResponseEntity.ok()
                .headers(headers)
                .body(status));
    }

    // Helper methods

    private String extractCorrelationId(ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst(CorrelationIdUtil.CORRELATION_HEADER);
        if (correlationId == null) {
            correlationId = request.getHeaders().getFirst(CorrelationIdUtil.REQUEST_HEADER);
        }
        return CorrelationIdUtil.generateCorrelationId(correlationId);
    }

    private void logAccess(ServerWebExchange exchange, String path, int status, String correlationId) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("method={} path={} status={} correlationId={}",
                request.getMethod(), path, status, correlationId);
    }
}
