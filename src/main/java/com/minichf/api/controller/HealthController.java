package com.minichf.api.controller;

import com.minichf.domain.model.HealthStatus;
import com.minichf.domain.model.ProblemDetails;
import com.minichf.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Nchf-ConvergedCharging API endpoints
 */
@Slf4j
@RestController
@RequestMapping("/nchf-convergedcharging/v2")
public class HealthController {

    @Value("${spring.application.name:nchf-converged-charging}")
    private String applicationName;

    /**
     * POST /chargingdata - Create endpoint stub
     */
    @PostMapping("/chargingdata")
    public Mono<ResponseEntity<ProblemDetails>> createChargingData(
            ServerWebExchange exchange,
            @RequestBody(required = false) String body) {
        
        String correlationId = extractCorrelationId(exchange.getRequest());
        String contentType = getContentType(exchange.getRequest());
        String accept = getAccept(exchange.getRequest());
        
        // Validate Content-Type
        if (contentType == null || !contentType.contains("application/json")) {
            logAccess(exchange, "POST /chargingdata", 415, correlationId);
            return buildErrorResponse(
                    415,
                    "Unsupported Media Type",
                    "Content-Type must be application/json",
                    "/chargingdata",
                    correlationId,
                    exchange
            );
        }

        // Validate Accept header
        if (!isAcceptable(accept, "application/json")) {
            logAccess(exchange, "POST /chargingdata", 406, correlationId);
            return buildErrorResponse(
                    406,
                    "Not Acceptable",
                    "Accept header must allow application/json",
                    "/chargingdata",
                    correlationId,
                    exchange
            );
        }

        // Validate request body is present
        if (body == null || body.trim().isEmpty()) {
            logAccess(exchange, "POST /chargingdata", 400, correlationId);
            return buildErrorResponse(
                    400,
                    "Bad Request",
                    "Request body must not be empty",
                    "/chargingdata",
                    correlationId,
                    exchange
            );
        }

        logAccess(exchange, "POST /chargingdata", 501, correlationId);
        return buildErrorResponse(
                501,
                "Not Implemented",
                "Create operation is not implemented yet",
                "/chargingdata",
                correlationId,
                exchange
        );
    }

    /**
     * OPTIONS /chargingdata
     */
    @RequestMapping(path = "/chargingdata", method = org.springframework.web.bind.annotation.RequestMethod.OPTIONS)
    public Mono<ResponseEntity<Void>> optionsChargingData(ServerWebExchange exchange) {
        String correlationId = extractCorrelationId(exchange.getRequest());
        logAccess(exchange, "OPTIONS /chargingdata", 204, correlationId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationIdUtil.CORRELATION_HEADER, correlationId);
        headers.set("Allow", "POST, OPTIONS");
        
        return Mono.just(ResponseEntity.noContent()
                .headers(headers)
                .build());
    }

    /**
     * POST /chargingdata/{ChargingDataRef}/update - Update endpoint stub
     */
    @PostMapping("/chargingdata/{ChargingDataRef}/update")
    public Mono<ResponseEntity<ProblemDetails>> updateChargingData(
            @PathVariable String ChargingDataRef,
            ServerWebExchange exchange,
            @RequestBody(required = false) String body) {
        
        String correlationId = extractCorrelationId(exchange.getRequest());
        
        // Validate UUID format
        if (!isValidUUID(ChargingDataRef)) {
            logAccess(exchange, "POST /chargingdata/{id}/update", 400, correlationId);
            return buildErrorResponse(
                    400,
                    "Bad Request",
                    "ChargingDataRef must be a valid UUID",
                    "/chargingdata/" + ChargingDataRef + "/update",
                    correlationId,
                    exchange
            );
        }

        String contentType = getContentType(exchange.getRequest());

        // Validate Content-Type
        if (contentType == null || !contentType.contains("application/json")) {
            logAccess(exchange, "POST /chargingdata/{id}/update", 415, correlationId);
            return buildErrorResponse(
                    415,
                    "Unsupported Media Type",
                    "Content-Type must be application/json",
                    "/chargingdata/" + ChargingDataRef + "/update",
                    correlationId,
                    exchange
            );
        }

        // Validate Accept header
        String accept = getAccept(exchange.getRequest());
        if (!isAcceptable(accept, "application/json")) {
            logAccess(exchange, "POST /chargingdata/{id}/update", 406, correlationId);
            return buildErrorResponse(
                    406,
                    "Not Acceptable",
                    "Accept header must allow application/json",
                    "/chargingdata/" + ChargingDataRef + "/update",
                    correlationId,
                    exchange
            );
        }

        // Validate request body is present
        if (body == null || body.trim().isEmpty()) {
            logAccess(exchange, "POST /chargingdata/{id}/update", 400, correlationId);
            return buildErrorResponse(
                    400,
                    "Bad Request",
                    "Request body must not be empty",
                    "/chargingdata/" + ChargingDataRef + "/update",
                    correlationId,
                    exchange
            );
        }

        logAccess(exchange, "POST /chargingdata/{id}/update", 501, correlationId);
        return buildErrorResponse(
                501,
                "Not Implemented",
                "Update operation is not implemented yet",
                "/chargingdata/" + ChargingDataRef + "/update",
                correlationId,
                exchange
        );
    }

    /**
     * OPTIONS /chargingdata/{ChargingDataRef}/update
     */
    @RequestMapping(path = "/chargingdata/{ChargingDataRef}/update", method = org.springframework.web.bind.annotation.RequestMethod.OPTIONS)
    public Mono<ResponseEntity<Void>> optionsUpdate(@PathVariable String ChargingDataRef, ServerWebExchange exchange) {
        String correlationId = extractCorrelationId(exchange.getRequest());
        logAccess(exchange, "OPTIONS /chargingdata/{id}/update", 204, correlationId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationIdUtil.CORRELATION_HEADER, correlationId);
        headers.set("Allow", "POST, OPTIONS");
        
        return Mono.just(ResponseEntity.noContent()
                .headers(headers)
                .build());
    }

    /**
     * POST /chargingdata/{ChargingDataRef}/release - Release endpoint stub
     */
    @PostMapping("/chargingdata/{ChargingDataRef}/release")
    public Mono<ResponseEntity<ProblemDetails>> releaseChargingData(
            @PathVariable String ChargingDataRef,
            ServerWebExchange exchange,
            @RequestBody(required = false) String body) {
        
        String correlationId = extractCorrelationId(exchange.getRequest());
        
        // Validate UUID format
        if (!isValidUUID(ChargingDataRef)) {
            logAccess(exchange, "POST /chargingdata/{id}/release", 400, correlationId);
            return buildErrorResponse(
                    400,
                    "Bad Request",
                    "ChargingDataRef must be a valid UUID",
                    "/chargingdata/" + ChargingDataRef + "/release",
                    correlationId,
                    exchange
            );
        }

        String contentType = getContentType(exchange.getRequest());

        // Validate Content-Type
        if (contentType == null || !contentType.contains("application/json")) {
            logAccess(exchange, "POST /chargingdata/{id}/release", 415, correlationId);
            return buildErrorResponse(
                    415,
                    "Unsupported Media Type",
                    "Content-Type must be application/json",
                    "/chargingdata/" + ChargingDataRef + "/release",
                    correlationId,
                    exchange
            );
        }

        // Validate Accept header
        String accept = getAccept(exchange.getRequest());
        if (!isAcceptable(accept, "application/json")) {
            logAccess(exchange, "POST /chargingdata/{id}/release", 406, correlationId);
            return buildErrorResponse(
                    406,
                    "Not Acceptable",
                    "Accept header must allow application/json",
                    "/chargingdata/" + ChargingDataRef + "/release",
                    correlationId,
                    exchange
            );
        }

        // Validate request body is present
        if (body == null || body.trim().isEmpty()) {
            logAccess(exchange, "POST /chargingdata/{id}/release", 400, correlationId);
            return buildErrorResponse(
                    400,
                    "Bad Request",
                    "Request body must not be empty",
                    "/chargingdata/" + ChargingDataRef + "/release",
                    correlationId,
                    exchange
            );
        }

        logAccess(exchange, "POST /chargingdata/{id}/release", 501, correlationId);
        return buildErrorResponse(
                501,
                "Not Implemented",
                "Release operation is not implemented yet",
                "/chargingdata/" + ChargingDataRef + "/release",
                correlationId,
                exchange
        );
    }

    /**
     * OPTIONS /chargingdata/{ChargingDataRef}/release
     */
    @RequestMapping(path = "/chargingdata/{ChargingDataRef}/release", method = org.springframework.web.bind.annotation.RequestMethod.OPTIONS)
    public Mono<ResponseEntity<Void>> optionsRelease(@PathVariable String ChargingDataRef, ServerWebExchange exchange) {
        String correlationId = extractCorrelationId(exchange.getRequest());
        logAccess(exchange, "OPTIONS /chargingdata/{id}/release", 204, correlationId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationIdUtil.CORRELATION_HEADER, correlationId);
        headers.set("Allow", "POST, OPTIONS");
        
        return Mono.just(ResponseEntity.noContent()
                .headers(headers)
                .build());
    }

    // Helper methods

    private String extractCorrelationId(ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst(CorrelationIdUtil.CORRELATION_HEADER);
        if (correlationId == null) {
            correlationId = request.getHeaders().getFirst(CorrelationIdUtil.REQUEST_HEADER);
        }
        return CorrelationIdUtil.generateCorrelationId(correlationId);
    }

    private String getContentType(ServerHttpRequest request) {
        return request.getHeaders().getContentType() != null ?
                request.getHeaders().getContentType().toString() : null;
    }

    private String getAccept(ServerHttpRequest request) {
        return request.getHeaders().getFirst(HttpHeaders.ACCEPT);
    }

    private boolean isAcceptable(String acceptHeader, String contentType) {
        if (acceptHeader == null || acceptHeader.trim().isEmpty()) {
            return true;
        }
        return acceptHeader.contains(contentType) || acceptHeader.contains("*/*");
    }

    private boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private Mono<ResponseEntity<ProblemDetails>> buildErrorResponse(
            int status,
            String title,
            String detail,
            String instance,
            String correlationId,
            ServerWebExchange exchange) {
        
        ProblemDetails problem = new ProblemDetails(status, title, detail, instance);
        problem.setCorrelationId(correlationId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationIdUtil.CORRELATION_HEADER, correlationId);
        headers.setContentType(MediaType.valueOf("application/problem+json"));
        
        return Mono.just(ResponseEntity.status(status)
                .headers(headers)
                .body(problem));
    }

    private void logAccess(ServerWebExchange exchange, String path, int status, String correlationId) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("method={} path={} status={} correlationId={}",
                request.getMethod(), path, status, correlationId);
    }
}
