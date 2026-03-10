package com.minichf.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Trigger model
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trigger {
    
    @JsonProperty("triggerType")
    private String triggerType;
    
    @JsonProperty("triggerCategory")
    private String triggerCategory;
}