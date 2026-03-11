package com.minichf.service;

import com.minichf.api.model.*;
import com.minichf.domain.model.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for generating ChargingDataResponse with default quota grants
 */
@Slf4j
@Service
public class ChargingDataResponseService {
    
    @Value("${quota.default.time:3600}")
    private Long defaultTimeQuota;
    
    @Value("${quota.default.volume.total:1048576}")
    private Long defaultTotalVolumeQuota;
    
    @Value("${quota.default.volume.uplink:524288}")
    private Long defaultUplinkVolumeQuota;
    
    @Value("${quota.default.volume.downlink:524288}")
    private Long defaultDownlinkVolumeQuota;
    
    @Value("${quota.default.service.specific.units:100}")
    private Long defaultServiceSpecificUnits;
    
    @Value("${session.failover.enabled:false}")
    private Boolean sessionFailoverEnabled;
    
    /**
     * Generate ChargingDataResponse with default quota grants
     */
    public ChargingDataResponse generateCreateResponse(SessionContext sessionContext, 
                                                      List<MultipleUnitUsage> multipleUnitUsage) {
        // Create the response with echoed invocation fields
        ChargingDataResponse response = ChargingDataResponse.builder()
                .invocationTimeStamp(sessionContext.getChargingDataRequest().getInvocationTimeStamp())
                .invocationSequenceNumber(sessionContext.getChargingDataRequest().getInvocationSequenceNumber())
                .invocationResult(InvocationResult.builder().build()) // Empty error field
                .multipleUnitInformation(createMultipleUnitInformation(multipleUnitUsage))
                .triggers(new ArrayList<>()) // Empty triggers array by default
                .sessionFailover(getSessionFailoverValue())
                .build();
        
        // Log the response creation
        log.info("event=nchf.create.response.sent, chargingDataRef={}, grantedRatingGroups={}", 
                sessionContext.getChargingDataRef(), 
                getRatingGroups(multipleUnitUsage));
        
        return response;
    }
    
    /**
     * Create MultipleUnitInformation entries for each MultipleUnitUsage
     */
    private List<MultipleUnitInformation> createMultipleUnitInformation(List<MultipleUnitUsage> multipleUnitUsage) {
        if (multipleUnitUsage == null || multipleUnitUsage.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<MultipleUnitInformation> multipleUnitInformationList = new ArrayList<>();
        
        for (MultipleUnitUsage usage : multipleUnitUsage) {
            MultipleUnitInformation mui = MultipleUnitInformation.builder()
                    .ratingGroup(usage.getRatingGroup())
                    .resultCode("SUCCESS")
                    .grantedUnit(GrantedUnit.builder()
                            .time(defaultTimeQuota)
                            .totalVolume(defaultTotalVolumeQuota)
                            .uplinkVolume(defaultUplinkVolumeQuota)
                            .downlinkVolume(defaultDownlinkVolumeQuota)
                            .serviceSpecificUnits(defaultServiceSpecificUnits)
                            .build())
                    .build();
            
            multipleUnitInformationList.add(mui);
        }
        
        return multipleUnitInformationList;
    }
    
    /**
     * Get session failover value based on configuration
     */
    private String getSessionFailoverValue() {
        return sessionFailoverEnabled ? "FAILOVER_SUPPORTED" : "FAILOVER_NOT_SUPPORTED";
    }
    
    /**
     * Get list of rating groups from multiple unit usage
     */
    private String getRatingGroups(List<MultipleUnitUsage> multipleUnitUsage) {
        if (multipleUnitUsage == null || multipleUnitUsage.isEmpty()) {
            return "";
        }
        
        StringBuilder ratingGroups = new StringBuilder();
        for (int i = 0; i < multipleUnitUsage.size(); i++) {
            if (i > 0) ratingGroups.append(",");
            ratingGroups.append(multipleUnitUsage.get(i).getRatingGroup());
        }
        
        return ratingGroups.toString();
    }
}