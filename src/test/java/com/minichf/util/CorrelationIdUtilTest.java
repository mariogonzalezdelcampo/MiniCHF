package com.minichf.util;

import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorrelationIdUtilTest {

    @Test
    void testGenerateCorrelationId_WithProvidedId_ShouldReturnProvided() {
        String providedId = "test-id-123";
        String result = CorrelationIdUtil.generateCorrelationId(providedId);
        assertThat(result).isEqualTo(providedId);
    }

    @Test
    void testGenerateCorrelationId_WithNullId_ShouldGenerateUUID() {
        String result = CorrelationIdUtil.generateCorrelationId(null);
        assertThat(result).isNotNull();
        assertThat(result).matches("[a-f0-9\\-]{36}"); // UUID format
    }

    @Test
    void testGenerateCorrelationId_WithEmptyId_ShouldGenerateUUID() {
        String result = CorrelationIdUtil.generateCorrelationId("");
        assertThat(result).isNotNull();
        assertThat(result).matches("[a-f0-9\\-]{36}");
    }

    @Test
    void testGenerateUUID_ShouldReturnValidUUID() {
        String result = CorrelationIdUtil.generateUUID();
        assertThat(result).isNotNull();
        assertThat(result).matches("[a-f0-9\\-]{36}");
    }

    @Test
    void testGenerateUUID_ShouldGenerateUnique() {
        Set<String> uuids = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            uuids.add(CorrelationIdUtil.generateUUID());
        }
        assertThat(uuids).hasSize(100); // All should be unique
    }

    @Test
    void testGetCurrentTimestamp_ShouldReturnRFC3339Format() {
        String timestamp = CorrelationIdUtil.getCurrentTimestamp();
        assertThat(timestamp).isNotNull();
        // RFC 3339 format check
        assertThat(timestamp).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.+");
    }

    @Test
    void testFormatTimestamp_ShouldReturnRFC3339Format() {
        OffsetDateTime now = OffsetDateTime.now();
        String result = CorrelationIdUtil.formatTimestamp(now);
        assertThat(result).isNotNull();
        assertThat(result).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.+");
    }

    @Test
    void testGetCurrentTimestamp_ShouldIncludeTimezone() {
        String timestamp = CorrelationIdUtil.getCurrentTimestamp();
        // Should have timezone info (either Z or +/-HH:MM)
        assertThat(timestamp).satisfiesAnyOf(
                t -> assertThat(t).endsWith("Z"),
                t -> assertThat(t).matches(".*[+-]\\d{2}:\\d{2}")
        );
    }
}
