package com.minichf.api.exception;

import com.minichf.domain.model.ProblemDetails;
import com.minichf.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

/**
 * Global exception handler for all REST controllers
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {

        String correlationId = CorrelationIdUtil.generateCorrelationId(null);
        String path = exchange.getRequest().getPath().value();

        log.error("Unexpected error: correlationId={} path={} exception={}",
                correlationId, path, ex.getMessage(), ex);

        ProblemDetails problem = new ProblemDetails(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred"
        );
        problem.setInstance(path);
        problem.setCorrelationId(correlationId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(problem);
    }

    /**
     * Handle request size limit exceptions
     */
    @ExceptionHandler(org.springframework.web.server.ServerWebInputException.class)
    public ResponseEntity<ProblemDetails> handleRequestSizeException(
            org.springframework.web.server.ServerWebInputException ex,
            ServerWebExchange exchange) {

        String correlationId = CorrelationIdUtil.generateCorrelationId(null);
        String path = exchange.getRequest().getPath().value();

        if (ex.getReason() != null && ex.getReason().contains("payload")) {
            log.warn("Payload too large: correlationId={} path={}", correlationId, path);
            
            ProblemDetails problem = new ProblemDetails(
                    HttpStatus.PAYLOAD_TOO_LARGE.value(),
                    "Payload Too Large",
                    "Request body exceeds maximum allowed size"
            );
            problem.setInstance(path);
            problem.setCorrelationId(correlationId);

            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .contentType(MediaType.valueOf("application/problem+json"))
                    .body(problem);
        }

        // Default to 400 Bad Request
        ProblemDetails problem = new ProblemDetails(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid request format"
        );
        problem.setInstance(path);
        problem.setCorrelationId(correlationId);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.valueOf("application/problem+json"))
                .body(problem);
    }
}
