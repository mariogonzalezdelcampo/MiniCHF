package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * NFIdentification model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NFIdentification {
    
    @JsonProperty("nodeFunctionality")
    private String nodeFunctionality;
    
    @JsonProperty("nFName")
    private String nFName;
    
    @JsonProperty("nFFqdn")
    private String nFFqdn;
    
    @JsonProperty("nFIPv4Address")
    private String nFIPv4Address;
    
    @JsonProperty("nFIPv6Address")
    private String nFIPv6Address;
    
    @JsonProperty("nFPLMNID")
    private PLMNID nFPLMNID;
}