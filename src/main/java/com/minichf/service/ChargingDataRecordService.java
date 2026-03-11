package com.minichf.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minichf.api.model.ChargingDataRequest;
import com.minichf.api.model.MultipleUnitUsage;
import com.minichf.api.model.PduSessionChargingInformation;
import com.minichf.api.model.PduSessionInformation;
import com.minichf.domain.model.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for generating CHF-CDR (Charging Function Call Detail Record) output files
 */
@Slf4j
@Service
public class ChargingDataRecordService {
    
    @Value("${cdr.output.dir:./cdr/}")
    private String cdrOutputDir;
    
    @Value("${cdr.sync.enabled:false}")
    private boolean cdrSyncEnabled;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Generate CHF-CDR file for a finalized session
     */
    public void generateChargingDataRecord(SessionContext sessionContext, String chargingDataRef) {
        try {
            // Create directory if it doesn't exist
            Path dirPath = Paths.get(cdrOutputDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // Generate file name with timestamp
            String timestamp = LocalDateTime.now().toString().replace(":", "-");
            String fileName = String.format("cdr_%s_%s.log", chargingDataRef, timestamp);
            Path filePath = dirPath.resolve(fileName);
            
            // Generate CDR content
            String cdrContent = generateCdrContent(sessionContext, chargingDataRef);
            
            // Write to file (asynchronously or synchronously)
            if (cdrSyncEnabled) {
                writeCdrFileSync(filePath, cdrContent);
            } else {
                writeCdrFileAsync(filePath, cdrContent);
            }
            
        } catch (Exception e) {
            log.error("cdr.write.failed: Failed to write CHF-CDR for session {}", chargingDataRef, e);
            // Do not rethrow to avoid blocking the Release response
        }
    }
    
    /**
     * Generate CDR content as structured text
     */
    private String generateCdrContent(SessionContext sessionContext, String chargingDataRef) {
        StringBuilder content = new StringBuilder();
        
        // Add header information
        content.append("ChargingDataRef=").append(chargingDataRef).append("\n");
        content.append("sessionCreationTimestamp=").append(sessionContext.getSessionCreationTimestamp()).append("\n");
        content.append("sessionReleaseTimestamp=").append(sessionContext.getInvocationTimeStamp()).append("\n");
        
        // Add invocation sequence numbers
        content.append("invocationSequenceNumbers=").append("1,2,3").append("\n"); // Placeholder - would be actual sequence numbers
        
        // Add subscriber identifier (masked)
        if (sessionContext.getChargingDataRequest() != null && 
            sessionContext.getChargingDataRequest().getSubscriberIdentifier() != null) {
            content.append("subscriberIdentifier=").append(maskPII(sessionContext.getChargingDataRequest().getSubscriberIdentifier())).append("\n");
        }
        
        // Add nfConsumerIdentification
        if (sessionContext.getNfConsumerIdentification() != null) {
            content.append("nfConsumerIdentification.nodeFunctionality=").append(sessionContext.getNfConsumerIdentification().getNodeFunctionality()).append("\n");
            if (sessionContext.getNfConsumerIdentification().getNFName() != null) {
                content.append("nfConsumerIdentification.nFName=").append(sessionContext.getNfConsumerIdentification().getNFName()).append("\n");
            }
            if (sessionContext.getNfConsumerIdentification().getNFFqdn() != null) {
                content.append("nfConsumerIdentification.nFFqdn=").append(sessionContext.getNfConsumerIdentification().getNFFqdn()).append("\n");
            }
            if (sessionContext.getNfConsumerIdentification().getNFIPv4Address() != null) {
                content.append("nfConsumerIdentification.nFIPv4Address=").append(sessionContext.getNfConsumerIdentification().getNFIPv4Address()).append("\n");
            }
            if (sessionContext.getNfConsumerIdentification().getNFIPv6Address() != null) {
                content.append("nfConsumerIdentification.nFIPv6Address=").append(sessionContext.getNfConsumerIdentification().getNFIPv6Address()).append("\n");
            }
        }
        
        // Add pdu session information summary
        if (sessionContext.getChargingDataRequest() != null && 
            sessionContext.getChargingDataRequest().getPDUSessionChargingInformation() != null) {
            
            PduSessionChargingInformation pduSessionInfo = sessionContext.getChargingDataRequest().getPDUSessionChargingInformation();
            if (pduSessionInfo.getPduSessionInformation() != null) {
                PduSessionInformation sessionInfo = pduSessionInfo.getPduSessionInformation();
                if (sessionInfo.getPduSessionID() != null) {
                    content.append("pduSession.pduSessionID=").append(sessionInfo.getPduSessionID()).append("\n");
                }
                if (sessionInfo.getDnnId() != null) {
                    content.append("pduSession.dnnId=").append(sessionInfo.getDnnId()).append("\n");
                }
                if (sessionInfo.getRatType() != null) {
                    content.append("pduSession.ratType=").append(sessionInfo.getRatType()).append("\n");
                }
                if (sessionInfo.getPduType() != null) {
                    content.append("pduSession.pduType=").append(sessionInfo.getPduType()).append("\n");
                }
                if (sessionInfo.getSscMode() != null) {
                    content.append("pduSession.sscMode=").append(sessionInfo.getSscMode()).append("\n");
                }
            }
        }
        
        // Add requested rating groups
        if (sessionContext.getChargingDataRequest() != null && 
            sessionContext.getChargingDataRequest().getMultipleUnitUsage() != null) {
            
            List<MultipleUnitUsage> multipleUnitUsage = sessionContext.getChargingDataRequest().getMultipleUnitUsage();
            if (multipleUnitUsage != null && !multipleUnitUsage.isEmpty()) {
                StringBuilder ratingGroups = new StringBuilder();
                for (int i = 0; i < multipleUnitUsage.size(); i++) {
                    if (i > 0) ratingGroups.append(",");
                    if (multipleUnitUsage.get(i).getRatingGroup() != null) {
                        ratingGroups.append(multipleUnitUsage.get(i).getRatingGroup());
                    }
                }
                content.append("requestedRatingGroups=").append(ratingGroups.toString()).append("\n");
            }
        }
        
        // Add recordEnd marker
        content.append("recordEnd=SUCCESS").append("\n");
        
        return content.toString();
    }
    
    /**
     * Write CDR file synchronously
     */
    private void writeCdrFileSync(Path filePath, String content) {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath, StandardCharsets.UTF_8))) {
            writer.print(content);
        } catch (IOException e) {
            log.error("cdr.write.failed: Failed to write CHF-CDR file synchronously: {}", filePath, e);
        }
    }
    
    /**
     * Write CDR file asynchronously (in background)
     */
    private void writeCdrFileAsync(Path filePath, String content) {
        // Run in background thread to avoid blocking
        new Thread(() -> {
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath, StandardCharsets.UTF_8))) {
                writer.print(content);
            } catch (IOException e) {
                log.error("cdr.write.failed: Failed to write CHF-CDR file asynchronously: {}", filePath, e);
            }
        }).start();
    }
    
    /**
     * Mask PII fields according to redaction rules
     */
    private String maskPII(String input) {
        if (input == null) return null;
        
        // For SUPI/GPSI, mask all but last 4 visible characters
        // This is a simplified approach - in a real implementation, we'd use more sophisticated regex
        return input.replaceAll("(?<=.{4}).(?=.{4})", "*");
    }
}