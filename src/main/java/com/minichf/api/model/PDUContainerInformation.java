package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * PDUContainerInformation model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PDUContainerInformation {
    
    @JsonProperty("timeofFirstUsage")
    private String timeofFirstUsage;
    
    @JsonProperty("timeofLastUsage")
    private String timeofLastUsage;
    
    @JsonProperty("qoSInformation")
    private String qoSInformation;
    
    @JsonProperty("qoSCharacteristics")
    private String qoSCharacteristics;
    
    @JsonProperty("afChargingIdentifier")
    private String afChargingIdentifier;
    
    @JsonProperty("userLocationInformation")
    private String userLocationInformation;
    
    @JsonProperty("uetimeZone")
    private String uetimeZone;
    
    @JsonProperty("rATType")
    private String rATType;
    
    @JsonProperty("servingNodeID")
    private List<ServingNetworkFunctionID> servingNodeID;
    
    @JsonProperty("presenceReportingAreaInformation")
    private Object presenceReportingAreaInformation;
    
    @JsonProperty("3gppPSDataOffStatus")
    private String _3gppPSDataOffStatus;
    
    @JsonProperty("sponsorIdentity")
    private String sponsorIdentity;
    
    @JsonProperty("applicationserviceProviderIdentity")
    private String applicationserviceProviderIdentity;
    
    @JsonProperty("chargingRuleBaseName")
    private String chargingRuleBaseName;
}