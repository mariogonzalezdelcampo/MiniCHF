package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * GrantedUnit model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrantedUnit {
    
    @JsonProperty("time")
    private Long time;
    
    @JsonProperty("totalVolume")
    private Long totalVolume;
    
    @JsonProperty("uplinkVolume")
    private Long uplinkVolume;
    
    @JsonProperty("downlinkVolume")
    private Long downlinkVolume;
    
    @JsonProperty("serviceSpecificUnits")
    private Long serviceSpecificUnits;
}