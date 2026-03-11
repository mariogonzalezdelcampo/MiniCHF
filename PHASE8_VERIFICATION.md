# Phase 8 Implementation Checklist

## Implementation Verification

### 1. Endpoint Registration
- [x] POST handler registered at `/chargingdata/{ChargingDataRef}/update`
- [x] Base path `/nchf-convergedcharging/v2` properly configured
- [x] Path parameter `ChargingDataRef` correctly extracted

### 2. Request Validation
- [x] UUID format validation implemented for ChargingDataRef
- [x] Content-Type validation implemented (`application/json` required)
- [x] Accept header validation implemented (`application/json` required)
- [x] Request body validation implemented (empty body returns 400)
- [x] Request size limits enforced (returns 413 on oversize)

### 3. Error Handling
- [x] 400 Bad Request returned for invalid UUID format
- [x] 415 Unsupported Media Type returned for invalid Content-Type
- [x] 406 Not Acceptable returned for unacceptable Accept header
- [x] 400 Bad Request returned for empty request body
- [x] 501 Not Implemented returned for valid syntax but unimplemented business logic

### 4. HTTP Methods
- [x] OPTIONS method implemented for `/chargingdata/{ChargingDataRef}/update`
- [x] OPTIONS returns 204 No Content
- [x] OPTIONS sets Allow header to `POST, OPTIONS`
- [x] Method not allowed returns 405 with proper Allow header

### 5. Logging and Metrics
- [x] Structured access logs implemented
- [x] Correlation ID included in all logs
- [x] Method, path, status code, and latency logged
- [x] Metrics collection supported for Update stub invocations

## Functionality Verification

### 1. Valid UUID with 501 Response
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: application/json" -d '{"test": "data"}'
```

Expected Response: 501 Not Implemented
```json
{
  "status": 501,
  "title": "Not Implemented",
  "detail": "Update operation is not implemented yet",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 501 status
- [x] Content-Type is "application/problem+json"
- [x] Response includes all required ProblemDetails fields
- [x] Location header present with correct URI

### 2. Invalid UUID Returns 400
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/invalid-uuid/update -H "Content-Type: application/json" -d '{"test": "data"}'
```

Expected Response: 400 Bad Request
```json
{
  "status": 400,
  "title": "Bad Request",
  "detail": "ChargingDataRef must be a valid UUID",
  "instance": "/chargingdata/invalid-uuid/update"
}
```

- [x] Returns 400 status
- [x] Content-Type is "application/problem+json"
- [x] Response includes all required ProblemDetails fields

### 3. Invalid Content-Type Returns 415
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: text/plain" -d "test data"
```

Expected Response: 415 Unsupported Media Type
```json
{
  "status": 415,
  "title": "Unsupported Media Type",
  "detail": "Content-Type must be application/json",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 415 status
- [x] Content-Type is "application/problem+json"
- [x] Response includes all required ProblemDetails fields

### 4. Unacceptable Accept Header Returns 406
```powershell
curl -X POST http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update -H "Content-Type: application/json" -H "Accept: application/xml" -d '{"test": "data"}'
```

Expected Response: 406 Not Acceptable
```json
{
  "status": 406,
  "title": "Not Acceptable",
  "detail": "Accept header must allow application/json",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 406 status
- [x] Content-Type is "application/problem+json"
- [x] Response includes all required ProblemDetails fields

### 5. OPTIONS Method Returns 204
```powershell
curl -X OPTIONS http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update
```

Expected Response: 204 No Content
- [x] Returns 204 status
- [x] Allow header set to "POST, OPTIONS"

### 6. Method Not Allowed Returns 405
```powershell
curl -X GET http://localhost:8080/nchf-convergedcharging/v2/chargingdata/123e4567-e89b-12d3-a456-426614174000/update
```

Expected Response: 405 Method Not Allowed
```json
{
  "status": 405,
  "title": "Method Not Allowed",
  "detail": "Method not allowed",
  "instance": "/chargingdata/123e4567-e89b-12d3-a456-426614174000/update"
}
```

- [x] Returns 405 status
- [x] Allow header set to "POST, OPTIONS"