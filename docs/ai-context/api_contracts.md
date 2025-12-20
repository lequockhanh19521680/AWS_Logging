# **API Contracts & Communication Standards**

Purpose: Defines how services communicate with clients and each other.  
Note to AI: Always follow these formats when generating Controllers or DTOs.

## **1\. General Standards**

- **Protocol:** REST over HTTP/1.1
- **Data Format:** JSON (camelCase keys)
- **Date Format:** ISO 8601 (yyyy-MM-dd'T'HH:mm:ss.SSSZ)
- **Currency:** Decimal (BigDecimal in Java), never Float/Double.

## **2\. Standard Response Wrapper**

All API responses must strictly follow this structure:

### **Success (200 OK)**

{  
 "status": 200,  
 "message": "Operation successful",  
 "data": {  
 // Actual payload here  
 "id": "123",  
 "balance": 500000  
 }  
}

### **Error (4xx, 5xx)**

{  
 "status": 400,  
 "error": "BAD_REQUEST",  
 "message": "Insufficient balance",  
 "path": "/api/v1/transfers",  
 "timestamp": "2025-12-20T10:00:00Z"  
}

## **3\. Key API Signatures**

### **Identity Service**

- POST /auth/login -> Login (2FA aware).
  - Request: { "username": "string", "password": "string" }
  - Response:
    - 2FA disabled: { "status": 200, "message": "OK", "data": { "accessToken": "...", "refreshToken": "..." } }
    - 2FA enabled: { "status": 200, "message": "OTP_SENT", "data": { "preAuthToken": "...", "expiresIn": 300 } }
- POST /auth/register -> Creates new user.
- POST /auth/verify-otp -> Verify OTP and exchange PRE_AUTH_TOKEN for JWT.
  - Request: { "preAuthToken": "string", "otp": "string" }
  - Response: { "status": 200, "message": "OK", "data": { "accessToken": "...", "refreshToken": "..." } }
- POST /auth/refresh -> Refresh Access Token.
  - Request: { "refreshToken": "string" }
  - Response: { "status": 200, "message": "OK", "data": { "accessToken": "..." } }
 
#### **Error Codes**
- INVALID_CREDENTIALS (401), USER_LOCKED (403), OTP_INVALID (401), OTP_EXPIRED (401), TOKEN_EXPIRED (401), RATE_LIMITED (429)

#### **Shared References**
- Authentication Flow: `../shared_list/auth/authentication.md`
- JWT Claims Contract: `../shared_list/auth/jwt_claims.md`
- Error Codes: `../shared_list/errors/error_codes.md`

### **Core Banking Service**

- GET /accounts/{id}/balance \-\> Returns current balance.
- POST /internal/ledger/entry \-\> **Internal Only**. Adds credit/debit entry (Atomic).

### **Payment Service**

- POST /transfers/initiate
  - **Input:** { "fromAccountId": "...", "toAccountId": "...", "amount": 1000 }
  - **Output:** { "transactionId": "...", "status": "PENDING" }
