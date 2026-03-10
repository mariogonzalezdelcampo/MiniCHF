package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * NetworkSlicingInfo model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkSlicingInfo {
    
    @JsonProperty("sNSSAI")
    private Snssai snssai;
    
    // ... other fields would be here based on the OpenAPI spec
}