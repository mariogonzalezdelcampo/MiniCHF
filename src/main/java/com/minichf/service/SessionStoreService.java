package com.minichf.service;

import com.minichf.domain.model.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    
    /**
     * Store a new session context
     */
    public void put(UUID chargingDataRef, SessionContext sessionContext) {
        sessionStore.put(chargingDataRef, sessionContext);
        log.info("Session created: chargingDataRef={}", chargingDataRef);
    }
    
    /**
     * Retrieve a session context by ChargingDataRef
     */
    public SessionContext get(UUID chargingDataRef) {
        SessionContext session = sessionStore.get(chargingDataRef);
        if (session != null) {
            log.debug("Session retrieved: chargingDataRef={}", chargingDataRef);
        } else {
            log.debug("Session not found: chargingDataRef={}", chargingDataRef);
        }
        return session;
    }
    
    /**
     * Update a session context
     */
    public void update(UUID chargingDataRef, SessionContext sessionContext) {
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
}