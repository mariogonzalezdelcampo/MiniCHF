package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MultipleUnitInformation model
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MultipleUnitInformation {
    
    @JsonProperty("ratingGroup")
    private Integer ratingGroup;
    
    @JsonProperty("resultCode")
    private String resultCode;
    
    @JsonProperty("grantedUnit")
    private GrantedUnit grantedUnit;
}