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
    private Boolean authorizedSessionAMBR;
    
    @JsonProperty("subscribedSessionAMBR")
    private Boolean subscribedSessionAMBR;
    
    @JsonProperty("networkSlicingInfo")
    private NetworkSlicingInfo networkSlicingInfo;
}