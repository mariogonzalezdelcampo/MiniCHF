package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Snssai model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Snssai {
    
    @JsonProperty("sst")
    private Integer sst;
    
    @JsonProperty("sd")
    private String sd;
}