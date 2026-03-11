package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * PduSessionChargingInformation model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PduSessionChargingInformation {
    
    @JsonProperty("chargingId")
    private String chargingId;
    
    @JsonProperty("homeProvidedChargingId")
    private String homeProvidedChargingId;
    
    @JsonProperty("userInformation")
    private UserInformation userInformation;
    
    @JsonProperty("userLocationinfo")
    private Object userLocationinfo;
    
    @JsonProperty("presenceReportingAreaInformation")
    private Map<String, Object> presenceReportingAreaInformation;
    
    @JsonProperty("uetimeZone")
    private String uetimeZone;
    
    @JsonProperty("pduSessionInformation")
    private PduSessionInformation pduSessionInformation;
    
    @JsonProperty("unitCountInactivityTimer")
    private Integer unitCountInactivityTimer;
    
    @JsonProperty("rANSecondaryRATUsageReport")
    private Object rANSecondaryRATUsageReport;
}