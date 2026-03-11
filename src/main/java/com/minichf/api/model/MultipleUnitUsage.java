package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * MultipleUnitUsage model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MultipleUnitUsage {
    
    @JsonProperty("ratingGroup")
    private Integer ratingGroup;
    
    @JsonProperty("requestedUnit")
    private RequestedUnit requestedUnit;
    
    @JsonProperty("usedUnitContainer")
    private List<UsedUnitContainer> usedUnitContainer;
    
    @JsonProperty("uPFID")
    private String uPFID;
}