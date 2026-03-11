package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * UserInformation model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInformation {
    
    @JsonProperty("servedGPSI")
    private String servedGPSI;
    
    @JsonProperty("servedPEI")
    private String servedPEI;
    
    @JsonProperty("unauthenticatedFlag")
    private Boolean unauthenticatedFlag;
    
    @JsonProperty("roamerInOut")
    private String roamerInOut;
}