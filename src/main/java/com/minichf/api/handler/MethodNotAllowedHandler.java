package com.minichf.api.handler;

import com.minichf.domain.model.ProblemDetails;
import com.minichf.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * Handler for unsupported HTTP methods (405 Method Not Allowed)
 */
@Slf4j
@Component
public class MethodNotAllowedHandler {

    private static final Map<String, String> ALLOWED_METHODS = new HashMap<>();
    static {
        ALLOWED_METHODS.put("/chargingdata", "POST, OPTIONS");
        ALLOWED_METHODS.put("/chargingdata/{ChargingDataRef}/update", "POST, OPTIONS");
        ALLOWED_METHODS.put("/chargingdata/{ChargingDataRef}/release", "POST, OPTIONS");
    }

    public Mono<ServerResponse> handleMethodNotAllowed(ServerRequest request) {
        String path = request.path();
        String method = request.methodName();
        String correlationId = extractCorrelationId(request);
        
        String allowedMethods = getAllowedMethods(path);
        
        log.warn("Method not allowed: method={} path={} correlationId={}", method, path, correlationId);
        
        ProblemDetails problem = new ProblemDetails(
                405,
                "Method Not Allowed",
                "HTTP method " + method + " is not allowed for this resource"
        );
        problem.setInstance(path);
        problem.setCorrelationId(correlationId);
        
        return ServerResponse.status(405)
                .contentType(MediaType.valueOf("application/problem+json"))
                .header("Allow", allowedMethods)
                .header("X-Correlation-ID", correlationId)
                .bodyValue(problem);
    }

    private String getAllowedMethods(String path) {
        for (Map.Entry<String, String> entry : ALLOWED_METHODS.entrySet()) {
            if (pathMatches(path, entry.getKey())) {
                return entry.getValue();
            }
        }
        return "GET, POST, PUT, DELETE, OPTIONS";
    }

    private boolean pathMatches(String actualPath, String pattern) {
        String regex = pattern.replace("{ChargingDataRef}", "[a-f0-9\\-]+");
        return actualPath.matches(regex);
    }

    private String extractCorrelationId(ServerRequest request) {
        String correlationId = request.headers().firstHeader("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = request.headers().firstHeader("X-Request-ID");
        }
        return CorrelationIdUtil.generateCorrelationId(correlationId);
    }
}
