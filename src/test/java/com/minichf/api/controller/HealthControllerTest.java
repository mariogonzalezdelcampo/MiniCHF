package com.minichf.api.controller;

import com.minichf.domain.model.ProblemDetails;
import com.minichf.util.CorrelationIdUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(controllers = {ProbeController.class, HealthController.class})
class HealthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        // Set up any test fixtures if needed
    }

    @Test
    void testGetHealth_ShouldReturn200() {
        webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void testGetReady_ShouldReturn200() {
        webTestClient.get()
                .uri("/ready")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo("READY")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void testPostChargingData_WithValidJson_ShouldReturn501() {
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"test\": \"data\"}")
                .exchange()
                .expectStatus().isEqualTo(501)
                .expectHeader().contentType(MediaType.valueOf("application/problem+json"))
                .expectBody()
                .jsonPath("$.status").isEqualTo(501)
                .jsonPath("$.title").exists()
                .jsonPath("$.detail").exists()
                .jsonPath("$.instance").exists();
    }

    @Test
    void testPostChargingData_WithoutContentType_ShouldReturn415() {
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata")
                .bodyValue("{\"test\": \"data\"}")
                .exchange()
                .expectStatus().isEqualTo(415)
                .expectHeader().contentType(MediaType.valueOf("application/problem+json"))
                .expectBody()
                .jsonPath("$.status").isEqualTo(415)
                .jsonPath("$.title").isEqualTo("Unsupported Media Type");
    }

    @Test
    void testPostChargingData_WithWrongContentType_ShouldReturn415() {
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("test data")
                .exchange()
                .expectStatus().isEqualTo(415);
    }

    @Test
    void testPostChargingData_WithUnacceptableAccept_ShouldReturn406() {
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept", "application/xml")
                .bodyValue("{\"test\": \"data\"}")
                .exchange()
                .expectStatus().isEqualTo(406)
                .expectHeader().contentType(MediaType.valueOf("application/problem+json"))
                .expectBody()
                .jsonPath("$.status").isEqualTo(406)
                .jsonPath("$.title").isEqualTo("Not Acceptable");
    }

    @Test
    void testPostChargingData_WithEmptyBody_ShouldReturn400() {
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.valueOf("application/problem+json"))
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.detail").isEqualTo("Request body must not be empty");
    }

    @Test
    void testOptionsChargingData_ShouldReturn204WithAllowHeader() {
        webTestClient.options()
                .uri("/nchf-convergedcharging/v2/chargingdata")
                .exchange()
                .expectStatus().isNoContent()
                .expectHeader().exists("Allow")
                .expectHeader().valueMatches("Allow", "POST, OPTIONS");
    }

    @Test
    void testPostUpdate_WithValidUUIDAndJson_ShouldReturn501() {
        String chargingDataRef = "123e4567-e89b-12d3-a456-426614174000";
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata/{ChargingDataRef}/update", chargingDataRef)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"test\": \"data\"}")
                .exchange()
                .expectStatus().isEqualTo(501);
    }

    @Test
    void testPostUpdate_WithInvalidUUID_ShouldReturn400() {
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata/invalid-uuid/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"test\": \"data\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.detail").isEqualTo("ChargingDataRef must be a valid UUID");
    }

    @Test
    void testPostRelease_WithValidUUIDAndJson_ShouldReturn501() {
        String chargingDataRef = "123e4567-e89b-12d3-a456-426614174000";
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata/{ChargingDataRef}/release", chargingDataRef)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"test\": \"data\"}")
                .exchange()
                .expectStatus().isEqualTo(501);
    }

    @Test
    void testPostRelease_WithInvalidUUID_ShouldReturn400() {
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata/invalid-uuid/release")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"test\": \"data\"}")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCorrelationIdGeneration_ShouldBeIncludedInResponse() {
        webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Correlation-ID");
    }

    @Test
    void testCorrelationIdPropagation_ShouldEchoProvidedId() {
        String providedCorrelationId = "test-correlation-id-12345";
        webTestClient.get()
                .uri("/health")
                .header("X-Correlation-ID", providedCorrelationId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("X-Correlation-ID", providedCorrelationId);
    }

    @Test
    void testProblemDetailsFormat_ShouldIncludeRequiredFields() {
        webTestClient.post()
                .uri("/nchf-convergedcharging/v2/chargingdata")
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("test")
                .exchange()
                .expectStatus().isEqualTo(415)
                .expectBody()
                .jsonPath("$.title").exists()
                .jsonPath("$.status").exists()
                .jsonPath("$.detail").exists()
                .jsonPath("$.instance").exists()
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.correlationId").exists();
    }

    @Test
    void testRFC3339TimestampFormat_ShouldBePresent() {
        webTestClient.get()
                .uri("/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.timestamp").value(timestamp -> {
                    assertThat(timestamp).isNotNull();
                    // Verify RFC 3339 format (ISO 8601 with timezone)
                    assertThat(timestamp.toString()).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.+");
                });
    }

    @Test
    void testOptionsUpdate_ShouldReturn204WithAllowHeader() {
        String chargingDataRef = "123e4567-e89b-12d3-a456-426614174000";
        webTestClient.options()
                .uri("/nchf-convergedcharging/v2/chargingdata/{ChargingDataRef}/update", chargingDataRef)
                .exchange()
                .expectStatus().isNoContent()
                .expectHeader().valueMatches("Allow", "POST, OPTIONS");
    }

    @Test
    void testOptionsRelease_ShouldReturn204WithAllowHeader() {
        String chargingDataRef = "123e4567-e89b-12d3-a456-426614174000";
        webTestClient.options()
                .uri("/nchf-convergedcharging/v2/chargingdata/{ChargingDataRef}/release", chargingDataRef)
                .exchange()
                .expectStatus().isNoContent()
                .expectHeader().valueMatches("Allow", "POST, OPTIONS");
    }
}
