package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ChargingDataResponse model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChargingDataResponse {
    
    @JsonProperty("invocationTimeStamp")
    private String invocationTimeStamp;
    
    @JsonProperty("invocationSequenceNumber")
    private Integer invocationSequenceNumber;
    
    @JsonProperty("invocationResult")
    private InvocationResult invocationResult;
    
    @JsonProperty("multipleUnitInformation")
    private List<MultipleUnitInformation> multipleUnitInformation;
    
    @JsonProperty("triggers")
    private List<Trigger> triggers;
    
    @JsonProperty("sessionFailover")
    private String sessionFailover;
}