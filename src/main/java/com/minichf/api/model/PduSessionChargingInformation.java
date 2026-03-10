package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * PduSessionChargingInformation model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PduSessionChargingInformation {
    
    @JsonProperty("pduSessionInformation")
    private PduSessionInformation pduSessionInformation;
    
    // ... other fields would be here based on the OpenAPI spec
}