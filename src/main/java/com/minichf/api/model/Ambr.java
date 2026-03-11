package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Ambr model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ambr {
    
    @JsonProperty("uplink")
    private String uplink;
    
    @JsonProperty("downlink")
    private String downlink;
}