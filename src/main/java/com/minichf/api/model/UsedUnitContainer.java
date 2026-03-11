package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * UsedUnitContainer model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsedUnitContainer {
    
    @JsonProperty("ratingGroup")
    private Integer ratingGroup;
    
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
    
    @JsonProperty("serviceId")
    private String serviceId;
    
    @JsonProperty("quotaManagementIndicator")
    private String quotaManagementIndicator;
    
    @JsonProperty("triggers")
    private List<Trigger> triggers;
    
    @JsonProperty("triggerTimestamp")
    private String triggerTimestamp;
    
    @JsonProperty("eventTimeStamps")
    private String eventTimeStamps;
    
    @JsonProperty("localSequenceNumber")
    private Integer localSequenceNumber;
    
    @JsonProperty("pDUContainerInformation")
    private PDUContainerInformation pDUContainerInformation;
}