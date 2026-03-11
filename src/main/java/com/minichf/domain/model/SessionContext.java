package com.minichf.domain.model;

import com.minichf.api.model.ChargingDataRequest;
import com.minichf.api.model.NFIdentification;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Session context for CHF operations
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionContext {
    
    private UUID chargingDataRef;
    
    private String sessionCreationTimestamp;
    
    private String invocationTimeStamp;
    
    private Integer invocationSequenceNumber;
    
    private NFIdentification nfConsumerIdentification;
    
    private ChargingDataRequest chargingDataRequest;
    
    private String state;
    
    private String correlationId;
}