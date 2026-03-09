package com.minichf.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class for correlation ID and timestamp handling
 */
public class CorrelationIdUtil {

    private static final String CORRELATION_ID = "X-Correlation-ID";
    private static final String REQUEST_ID = "X-Request-ID";

    /**
     * Generate a new correlation/request ID if not provided
     */
    public static String generateCorrelationId(String providedId) {
        if (providedId != null && !providedId.trim().isEmpty()) {
            return providedId;
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a UUID v4 string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Get current timestamp in RFC 3339 format
     */
    public static String getCurrentTimestamp() {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * Format OffsetDateTime to RFC 3339
     */
    public static String formatTimestamp(OffsetDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static final String CORRELATION_HEADER = CORRELATION_ID;
    public static final String REQUEST_HEADER = REQUEST_ID;
}
