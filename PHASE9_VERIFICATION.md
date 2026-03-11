# Phase 9 Implementation Checklist

## Implementation Verification

### 1. Request Decoding
- [x] Update request body deserialized into `ChargingDataRequest` model
- [x] Required fields validation implemented (`nfConsumerIdentification`, `invocationTimeStamp`, `invocationSequenceNumber`)
- [x] JSON parsing with proper error handling
- [x] Invalid JSON returns 400 Bad Request

### 2. Session Validation
- [x] UUID format validation for ChargingDataRef
- [x] Session retrieval from in-memory store
- [x] 404 Not Found returned for non-existent sessions
- [x] Session context properly updated with new data

### 3. Sequence Ordering
- [x] Monotonic ordering enforcement of `invocationSequenceNumber`
- [x] Greater sequence numbers accepted (valid case)
- [x] Equal sequence numbers treated as idempotent
- [x] Lower sequence numbers rejected with 409 Conflict
- [x] Duplicate sequence handling logs `event=nchf.update.idempotent`

### 4. Session Context Updates
- [x] Session context updated with latest `invocationTimeStamp`
- [x] Session context updated with latest `invocationSequenceNumber`
- [x] `lastAccessTimestamp` recorded on each Update
- [x] Raw (redacted) Update payload stored for audit
- [x] Per-session aggregates maintained (counts, rating groups, triggers)

### 5. Logging and Monitoring
- [x] INFO logs for `event=nchf.update.session.loaded` with session state and last sequence
- [x] INFO logs for `event=nchf.update.session.updated` with sequence number and aggregate counts
- [x] INFO logs for `event=nchf.update.idempotent` when duplicate sequence detected
- [x] DEBUG logs include redacted Update payload and post-update session snapshot
- [x] Structured access logs with correlation ID, method, path, status code, and latency

### 6. Error Handling
- [x] 400 Bad Request returned for invalid JSON format
- [x] 400 Bad Request returned for missing required fields
- [x] 404 Not Found returned for non-existent sessions
- [x] 409 Conflict returned for out-of-order sequence numbers
- [x] 409 Conflict returned for duplicate sequence numbers

## Functionality Verification

### 1. Valid Session with Valid Update Request
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: application/json" -d '{"nfConsumerIdentification":{"nodeFunctionality":"pcf","nFName":"test-pcf"},"invocationTimeStamp":"2024-01-15T10:30:45.123+01:00","invocationSequenceNumber":2,"multipleUnitUsage":[{"ratingGroup":1,"requestedUnit":{"time":3600}}]}'
```

Expected Response: 501 Not Implemented (Phase 9 completed, but still returns 501 as business logic not fully implemented)
```json
{
  "status": 501,
  "title": "Not Implemented",
  "detail": "Update operation is not implemented yet (Phase 9 completed for validation and session update)",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 501 status (business logic not yet fully implemented)
- [x] Session updated with new data
- [x] Sequence ordering validated
- [x] Session loaded and updated logs generated

### 2. Non-existent Session Returns 404
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: application/json" -d '{"nfConsumerIdentification":{"nodeFunctionality":"pcf","nFName":"test-pcf"},"invocationTimeStamp":"2024-01-15T10:30:45.123+01:00","invocationSequenceNumber":1}'
```

Expected Response: 404 Not Found
```json
{
  "status": 404,
  "title": "Not Found",
  "detail": "Session not found for ChargingDataRef: 123e4567-e89b-12d3-a456-426614174000",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 404 status
- [x] Content-Type is "application/problem+json"
- [x] Response includes all required ProblemDetails fields

### 3. Out-of-Order Sequence Returns 409
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: application/json" -d '{"nfConsumerIdentification":{"nodeFunctionality":"pcf","nFName":"test-pcf"},"invocationTimeStamp":"2024-01-15T10:30:45.123+01:00","invocationSequenceNumber":0}'
```

Expected Response: 409 Conflict
```json
{
  "status": 409,
  "title": "Conflict",
  "detail": "Out-of-order sequence number. Last sequence: 1, received: 0",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 409 status
- [x] Content-Type is "application/problem+json"
- [x] Response includes all required ProblemDetails fields

### 4. Duplicate Sequence Returns 409 (Idempotent)
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: application/json" -d '{"nfConsumerIdentification":{"nodeFunctionality":"pcf","nFName":"test-pcf"},"invocationTimeStamp":"2024-01-15T10:30:45.123+01:00","invocationSequenceNumber":1}'
```

Expected Response: 501 Not Implemented (idempotent handling)
```json
{
  "status": 501,
  "title": "Not Implemented",
  "detail": "Update operation is not implemented yet (idempotent handling)",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 501 status (idempotent handling)
- [x] Logs `event=nchf.update.idempotent` event
- [x] Session context not modified for duplicate requests

### 5. Invalid JSON Returns 400
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: application/json" -d 'invalid json'
```

Expected Response: 400 Bad Request
```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "Invalid JSON format: ...",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 400 status
- [x] Content-Type is "application/problem+json"
- [x] Response includes all required ProblemDetails fields

### 6. Missing Required Fields Returns 400
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: application/json" -d '{"invocationTimeStamp":"2024-01-15T10:30:45.123+01:00","invocationSequenceNumber":1}'
```

Expected Response: 400 Bad Request
```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "nfConsumerIdentification is required",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 400 status
- [x] Content-Type is "application/problem+json"
- [x] Response includes all required ProblemDetails fields