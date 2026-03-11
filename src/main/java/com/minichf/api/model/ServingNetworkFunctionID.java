package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * ServingNetworkFunctionID model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServingNetworkFunctionID {
    
    @JsonProperty("servingNetworkFunctionInformation")
    private NFIdentification servingNetworkFunctionInformation;
    
    @JsonProperty("aMFId")
    private String aMFId;
}