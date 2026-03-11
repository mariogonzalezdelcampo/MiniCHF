package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * PduSessionInformation model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PduSessionInformation {
    
    @JsonProperty("pduSessionID")
    private Integer pduSessionID;
    
    @JsonProperty("dnnId")
    private String dnnId;
    
    @JsonProperty("ratType")
    private String ratType;
    
    @JsonProperty("pduType")
    private String pduType;
    
    @JsonProperty("sscMode")
    private String sscMode;
    
    @JsonProperty("authorizedSessionAMBR")
    private Ambr authorizedSessionAMBR;
    
    @JsonProperty("subscribedSessionAMBR")
    private Ambr subscribedSessionAMBR;
    
    @JsonProperty("networkSlicingInfo")
    private NetworkSlicingInfo networkSlicingInfo;
    
    @JsonProperty("hPlmnId")
    private PLMNID hPlmnId;
    
    @JsonProperty("servingNetworkFunctionID")
    private ServingNetworkFunctionID servingNetworkFunctionID;
    
    @JsonProperty("chargingCharacteristics")
    private String chargingCharacteristics;
    
    @JsonProperty("chargingCharacteristicsSelectionMode")
    private String chargingCharacteristicsSelectionMode;
    
    @JsonProperty("chargingRuleBaseName")
    private String chargingRuleBaseName;
    
    @JsonProperty("startTime")
    private String startTime;
    
    @JsonProperty("3gppPSDataOffStatus")
    private String _3gppPSDataOffStatus;
    
    @JsonProperty("pduAddress")
    private Object pduAddress;
    
    @JsonProperty("diagnostics")
    private Object diagnostics;
    
    @JsonProperty("authorizedQoSInformation")
    private Object authorizedQoSInformation;
    
    @JsonProperty("subscribedQoSInformation")
    private Object subscribedQoSInformation;
    
    @JsonProperty("servingCNPlmnId")
    private PLMNID servingCNPlmnId;
}