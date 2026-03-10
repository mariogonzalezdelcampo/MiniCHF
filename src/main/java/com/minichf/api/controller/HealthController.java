package com.minichf.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minichf.api.model.ChargingDataRequest;
import com.minichf.api.model.NetworkSlicingInfo;
import com.minichf.api.model.Snssai;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Log decoded ChargingDataRequest with proper redaction rules
     */
    private void logDecodedRequest(ChargingDataRequest request, String correlationId, ServerHttpRequest httpRequest) {
        // Create a structured log entry for the decoded request
        StringBuilder logMessage = new StringBuilder();
        logMessage.append("event=nchf.create.request.decoded");
        logMessage.append(", corrId=").append(correlationId);
        logMessage.append(", invocationTimeStamp=").append(request.getInvocationTimeStamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logMessage.append(", invocationSequenceNumber=").append(request.getInvocationSequenceNumber());
        
        // Add nfConsumerIdentification if present
        if (request.getNfConsumerIdentification() != null) {
            if (request.getNfConsumerIdentification().getNodeFunctionality() != null) {
                logMessage.append(", nf.nodeFunctionality=").append(request.getNfConsumerIdentification().getNodeFunctionality());
            }
            if (request.getNfConsumerIdentification().getNFName() != null) {
                logMessage.append(", nf.nFName=").append(request.getNfConsumerIdentification().getNFName());
            }
            if (request.getNfConsumerIdentification().getNFFqdn() != null) {
                logMessage.append(", nf.nFFqdn=").append(request.getNfConsumerIdentification().getNFFqdn());
            }
            if (request.getNfConsumerIdentification().getNFIPv4Address() != null) {
                logMessage.append(", nf.nFIPv4Address=").append(request.getNfConsumerIdentification().getNFIPv4Address());
            }
            if (request.getNfConsumerIdentification().getNFIPv6Address() != null) {
                logMessage.append(", nf.nFIPv6Address=").append(request.getNfConsumerIdentification().getNFIPv6Address());
            }
        }
        
        // Add subscriberIdentifier with redaction
        if (request.getSubscriberIdentifier() != null) {
            logMessage.append(", subscriberIdentifier=").append(redactPII(request.getSubscriberIdentifier()));
        }
        
        // Add oneTimeEvent and oneTimeEventType
        if (request.getOneTimeEvent() != null) {
            logMessage.append(", oneTimeEvent=").append(request.getOneTimeEvent());
        }
        if (request.getOneTimeEventType() != null) {
            logMessage.append(", oneTimeEventType=").append(request.getOneTimeEventType());
        }
        
        // Add counts for multipleUnitUsage
        if (request.getMultipleUnitUsage() != null) {
            logMessage.append(", multipleUnitUsage.count=").append(request.getMultipleUnitUsage().size());
            
            // Extract rating groups
            StringBuilder ratingGroups = new StringBuilder();
            for (int i = 0; i < request.getMultipleUnitUsage().size(); i++) {
                if (request.getMultipleUnitUsage().get(i).getRatingGroup() != null) {
                    if (ratingGroups.length() > 0) ratingGroups.append(",");
                    ratingGroups.append(request.getMultipleUnitUsage().get(i).getRatingGroup());
                }
            }
            if (ratingGroups.length() > 0) {
                logMessage.append(", requestedRatingGroups=").append(ratingGroups.toString());
            }
        }
        
        // Add counts for triggers
        if (request.getTriggers() != null) {
            logMessage.append(", triggers.count=").append(request.getTriggers().size());
        }
        
        // Add pduSession information if present
        if (request.getPDUSessionChargingInformation() != null && 
            request.getPDUSessionChargingInformation().getPduSessionInformation() != null) {
            
            var pduInfo = request.getPDUSessionChargingInformation().getPduSessionInformation();
            if (pduInfo.getPduSessionID() != null) {
                logMessage.append(", pduSession.pduSessionID=").append(pduInfo.getPduSessionID());
            }
            if (pduInfo.getDnnId() != null) {
                logMessage.append(", pduSession.dnnId=").append(pduInfo.getDnnId());
            }
            if (pduInfo.getRatType() != null) {
                logMessage.append(", pduSession.ratType=").append(pduInfo.getRatType());
            }
            if (pduInfo.getPduType() != null) {
                logMessage.append(", pduSession.pduType=").append(pduInfo.getPduType());
            }
            if (pduInfo.getSscMode() != null) {
                logMessage.append(", pduSession.sscMode=").append(pduInfo.getSscMode());
            }
            if (pduInfo.getNetworkSlicingInfo() != null && pduInfo.getNetworkSlicingInfo().getSnssai() != null) {
                Snssai snssai = pduInfo.getNetworkSlicingInfo().getSnssai();
                if (snssai.getSst() != null) {
                    logMessage.append(", pduSession.snssai.sst=").append(snssai.getSst());
                }
                if (snssai.getSd() != null) {
                    logMessage.append(", pduSession.snssai.sd=").append(snssai.getSd());
                }
            }
        }
        
        // Log the structured message
        log.info(logMessage.toString());
        
        // Log detailed DEBUG entry with redacted full request payload
        try {
            String fullRequestJson = objectMapper.writeValueAsString(request);
            log.debug("nchf.create.request.decoded.debug: corrId={}, fullRequest={}", correlationId, redactPII(fullRequestJson));
        } catch (Exception e) {
            log.warn("Failed to serialize full request for debug logging", e);
        }
    }
    
    /**
     * Redact PII fields in log messages
     */
    private String redactPII(String input) {
        if (input == null) return null;
        
        // For SUPI/GPSI, mask all but last 4 visible characters
        // This is a simplified approach - in a real implementation, we'd use more sophisticated regex
        return input.replaceAll("(?<=.{4}).(?=.{4})", "*");
    }

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

        // Phase 4: Decode ChargingDataRequest payload fields and log them
        try {
            // Deserialize the request body into ChargingDataRequest model
            ChargingDataRequest chargingDataRequest = objectMapper.readValue(body, ChargingDataRequest.class);
            
            // Validate required fields
            if (chargingDataRequest.getNfConsumerIdentification() == null) {
                logAccess(exchange, "POST /chargingdata", 400, correlationId);
                return buildErrorResponse(
                        400,
                        "Bad Request",
                        "nfConsumerIdentification is required",
                        "/chargingdata",
                        correlationId,
                        exchange
                );
            }
            
            if (chargingDataRequest.getInvocationTimeStamp() == null) {
                logAccess(exchange, "POST /chargingdata", 400, correlationId);
                return buildErrorResponse(
                        400,
                        "Bad Request",
                        "invocationTimeStamp is required",
                        "/chargingdata",
                        correlationId,
                        exchange
                );
            }
            
            if (chargingDataRequest.getInvocationSequenceNumber() == null) {
                logAccess(exchange, "POST /chargingdata", 400, correlationId);
                return buildErrorResponse(
                        400,
                        "Bad Request",
                        "invocationSequenceNumber is required",
                        "/chargingdata",
                        correlationId,
                        exchange
                );
            }
            
            // Log the decoded request with proper redaction rules (Phase 4)
            logDecodedRequest(chargingDataRequest, correlationId, exchange.getRequest());
            
            // Continue with Phase 3 behavior - return 501 Not Implemented
            logAccess(exchange, "POST /chargingdata", 501, correlationId);
            return buildErrorResponse(
                    501,
                    "Not Implemented",
                    "Create operation is not implemented yet",
                    "/chargingdata",
                    correlationId,
                    exchange
            );
        } catch (Exception e) {
            // If we get here, it means the JSON parsing failed or validation failed
            logAccess(exchange, "POST /chargingdata", 400, correlationId);
            return buildErrorResponse(
                    400,
                    "Bad Request",
                    "Invalid JSON format: " + e.getMessage(),
                    "/chargingdata",
                    correlationId,
                    exchange
            );
        }
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
