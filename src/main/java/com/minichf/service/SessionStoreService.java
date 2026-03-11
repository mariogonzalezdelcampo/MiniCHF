package com.minichf.service;

import com.minichf.domain.model.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * In-memory session store for CHF operations
 */
@Slf4j
@Service
public class SessionStoreService {
    
    private final Map<UUID, SessionContext> sessionStore = new ConcurrentHashMap<>();
    
    @Value("${session.ttl.seconds:-1}")
    private int sessionTtlSeconds;
    
    @Value("${session.overwrite.enabled:false}")
    private boolean sessionOverwriteEnabled;
    
    /**
     * Store a new session context
     */
    public void put(UUID chargingDataRef, SessionContext sessionContext) {
        // Check if session already exists and handle overwrite policy
        if (sessionStore.containsKey(chargingDataRef)) {
            if (sessionOverwriteEnabled) {
                log.warn("Overwriting existing session: chargingDataRef={}", chargingDataRef);
            } else {
                log.warn("Session already exists, not overwriting: chargingDataRef={}", chargingDataRef);
                return;
            }
        }
        
        // Set last access timestamp
        sessionContext.setLastAccessTimestamp(LocalDateTime.now().toString());
        sessionStore.put(chargingDataRef, sessionContext);
        log.info("Session created: chargingDataRef={}", chargingDataRef);
    }
    
    /**
     * Retrieve a session context by ChargingDataRef
     */
    public SessionContext get(UUID chargingDataRef) {
        SessionContext session = sessionStore.get(chargingDataRef);
        if (session != null) {
            // Check TTL expiration if enabled
            if (sessionTtlSeconds > 0) {
                // For simplicity, we'll implement lazy expiration check
                // In a real implementation, this would be more sophisticated
                log.debug("Session retrieved: chargingDataRef={}", chargingDataRef);
            } else {
                // Update last access timestamp for non-expiring sessions
                session.setLastAccessTimestamp(LocalDateTime.now().toString());
                log.debug("Session retrieved: chargingDataRef={}", chargingDataRef);
            }
        } else {
            log.debug("Session not found: chargingDataRef={}", chargingDataRef);
        }
        return session;
    }
    
    /**
     * Update a session context
     */
    public void update(UUID chargingDataRef, SessionContext sessionContext) {
        // Update last access timestamp
        sessionContext.setLastAccessTimestamp(LocalDateTime.now().toString());
        sessionStore.put(chargingDataRef, sessionContext);
        log.info("Session updated: chargingDataRef={}", chargingDataRef);
    }
    
    /**
     * Remove a session context
     */
    public void remove(UUID chargingDataRef) {
        SessionContext removed = sessionStore.remove(chargingDataRef);
        if (removed != null) {
            log.info("Session removed: chargingDataRef={}", chargingDataRef);
        } else {
            log.debug("Session not found for removal: chargingDataRef={}", chargingDataRef);
        }
    }
    
    /**
     * Get current session count
     */
    public int getSessionCount() {
        return sessionStore.size();
    }
    
    /**
     * Check if session exists and is not expired
     */
    public boolean isSessionValid(UUID chargingDataRef) {
        SessionContext session = sessionStore.get(chargingDataRef);
        if (session == null) {
            return false;
        }
        
        // If TTL is not enabled, session is always valid
        if (sessionTtlSeconds <= 0) {
            return true;
        }
        
        // For simplicity, we're not implementing full TTL logic here
        // In a real implementation, this would check if session has expired
        return true;
    }
}