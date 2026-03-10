package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ChargingDataRequest model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChargingDataRequest {
    
    @JsonProperty("subscriberIdentifier")
    private String subscriberIdentifier;
    
    @JsonProperty("nfConsumerIdentification")
    private NFIdentification nfConsumerIdentification;
    
    @JsonProperty("invocationTimeStamp")
    private LocalDateTime invocationTimeStamp;
    
    @JsonProperty("invocationSequenceNumber")
    private Integer invocationSequenceNumber;
    
    @JsonProperty("oneTimeEvent")
    private Boolean oneTimeEvent;
    
    @JsonProperty("oneTimeEventType")
    private String oneTimeEventType;
    
    @JsonProperty("notifyUri")
    private String notifyUri;
    
    @JsonProperty("serviceSpecificationInfo")
    private String serviceSpecificationInfo;
    
    @JsonProperty("multipleUnitUsage")
    private List<MultipleUnitUsage> multipleUnitUsage;
    
    @JsonProperty("triggers")
    private List<Trigger> triggers;
    
    @JsonProperty("pDUSessionChargingInformation")
    private PduSessionChargingInformation pDUSessionChargingInformation;
    
    // ... other fields would be here based on the OpenAPI spec
}