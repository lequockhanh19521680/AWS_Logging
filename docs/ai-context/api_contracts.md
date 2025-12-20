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

- POST /auth/login \-\> Returns JWT Access Token \+ Refresh Token.
- POST /auth/register \-\> Creates new user.

### **Core Banking Service**

- GET /accounts/{id}/balance \-\> Returns current balance.
- POST /internal/ledger/entry \-\> **Internal Only**. Adds credit/debit entry (Atomic).

### **Payment Service**

- POST /transfers/initiate
  - **Input:** { "fromAccountId": "...", "toAccountId": "...", "amount": 1000 }
  - **Output:** { "transactionId": "...", "status": "PENDING" }
