package com.minichf.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Health status response for GET /health endpoint
 */
public class HealthStatus {

    @JsonProperty("status")
    private String status;

    @JsonProperty("timestamp")
    private String timestamp;

    public HealthStatus(String status, String timestamp) {
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
