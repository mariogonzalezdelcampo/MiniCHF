package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * PLMNID model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PLMNID {
    
    @JsonProperty("mcc")
    private String mcc;
    
    @JsonProperty("mnc")
    private String mnc;
}