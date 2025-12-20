# **Detail Design: API Gateway (Security-First Architecture)**

## **1\. Overview**

The API Gateway is the Single Entry Point for all clients (Web Portal, Back Office, Mobile App, 3rd Party Partners).

Design Philosophy: "Zero Trust" & "Defense in Depth"  
The Gateway does not trust any request. Every request must be authenticated, authorized, scanned for malicious content, and rate-limited before being routed to the internal network.

## **2\. Technology Stack**

- **Framework:** Spring Cloud Gateway (built on Spring Boot 3 & Project Reactor \- Non-blocking I/O).
- **Security:** Spring Security (Reactive WebFlux).
- **Resilience:** Resilience4j (Circuit Breaker, Time Limiter).
- **Rate Limiting:** Redis (Token Bucket Algorithm) \- _Mandatory for synchronizing limits across Gateway instances_.
- **Observability:** Micrometer Tracing (generates Trace ID/Correlation ID).

## **3\. Multi-Layer Security Architecture**

The protection system is divided into 4 layers at the Gateway:

### **Layer 1: Network & Transport Protection**

- **TLS/SSL Termination:** Only accept traffic via HTTPS (Port 443). Block all HTTP (Port 80\) or redirect to HTTPS.
- **Strict Transport Security (HSTS):** Enforce browsers to always use HTTPS to prevent Protocol Downgrade attacks.
- **Request Size Limiting:** Limit Body size (e.g., max 1MB for JSON) to prevent Buffer Overflow DoS attacks.

### **Layer 2: Traffic Control (Anti-DDoS)**

- **Global Rate Limiting:** Limit total requests to the system (e.g., 10,000 req/s).
- **User/IP Rate Limiting:**
  - _Anonymous:_ Limit by IP (e.g., 10 req/s).
  - _Authenticated:_ Limit by User ID (e.g., 50 req/s).
  - _Partner:_ Limit by Client ID (Specific SLA).
- **Algorithm:** Token Bucket (Redis \+ Lua Script) to ensure high performance.

### **Layer 3: Authentication & Authorization (The Gatekeeper)**

Instead of letting Microservices validate Tokens themselves (which is redundant), the Gateway acts as a **Policy Enforcement Point (PEP)**:

1. **Stateless JWT Validation:**
   - Gateway loads **Public Key** from Identity Service at startup.
   - Verifies the Digital Signature, Expiration (Exp), and Algorithm (Alg) of the JWT right at the Gateway.
   - _Benefit:_ Garbage requests/fake tokens are blocked immediately, saving backend resources.
2. **Blacklist Check (Stateful):**
   - Checks jti (JWT ID) in Redis to see if the token has been revoked (Logout) or banned.
3. **Header Sanitization:**
   - Removes sensitive headers sent by the Client (e.g., X-User-Role, X-Internal-Id) to prevent spoofing.
   - Automatically injects X-User-Id, X-Roles headers after successful validation for downstream Microservices to use.

### **Layer 4: Web Application Firewall (WAF) Logic (Basic)**

- **CORS (Cross-Origin Resource Sharing):** Only allow 2 domains: web-portal.fintech.com and back-office.fintech.com. Block \* completely.
- **Security Headers:** Automatically add security headers to the response:
  - X-Content-Type-Options: nosniff
  - X-Frame-Options: DENY (Prevent Clickjacking)
  - X-XSS-Protection: 1; mode=block

## **4\. Routing Topology**

| Path Prefix         | Target Service       | Auth Required      | Notes                                   |
| :------------------ | :------------------- | :----------------- | :-------------------------------------- |
| /auth/\*\*          | identity-service     | ❌ Public          | Login, Register, Refresh Token          |
| /users/\*\*         | identity-service     | ✅ Secured         | User Profile, Management                |
| /accounts/\*\*      | core-banking-service | ✅ Secured         | View balance, history                   |
| /transfers/\*\*     | payment-service      | ✅ Secured         | Fund transfer                           |
| /notifications/\*\* | notification-service | ✅ Secured         | Get noti history, Register device token |
| /audits/\*\*        | audit-log-service    | ✅ Secured (Admin) | View system logs (Back Office use only) |
| /internal/\*\*      | ❌ **BLOCK**         | ⛔ Forbidden       | Block external access to internal APIs  |

## **5\. Error Handling & Resilience Scenarios**

### **5.1. Circuit Breaker**

- **Problem:** If payment-service hangs, the Gateway must not hang with it (Cascading Failure).
- **Solution:** Configure Resilience4j for each route.
  - If error rate \> 50% or response time \> 5s \-\> **Open Circuit** (Cut connection immediately).
  - Return a friendly Fallback Response: "Payment service is under maintenance, please try again later".

### **5.2. Correlation ID (Tracing)**

- Every request entering the Gateway is assigned a unique UUID in the X-Correlation-Id header.
- This ID is passed through Identity \-\> Payment \-\> Core Banking \-\> Notification.
- Helps trace errors in centralized logs (ELK Stack/AWS CloudWatch).

## **6\. Detailed Request Flow (Sequence Flow)**

1. **Client** calls POST /transfers.
2. **Gateway** intercepts the request:
   - Check 1: Is IP Blacklisted?
   - Check 2: Is Rate Limit quota available for this IP/User? (Check Redis).
   - Check 3: Is JWT valid? (Verify Signature & Expire).
3. **Gateway** processes Headers:
   - Remove: X-Forwarded-For (spoofing), Authorization (if using another internal Token Relay mechanism).
   - Add: X-Correlation-Id, X-User-Id (extracted from JWT claims).
4. **Gateway** forwards request to payment-service.
5. **Payment Service** processes and returns result.
6. **Gateway** receives response, adds Security Headers, and returns to Client.

## **7\. Configuration Snippet (application.yaml)**

spring:  
 cloud:  
 gateway:  
 routes:  
 \- id: payment-service  
 uri: lb://payment-service \# Load Balancing  
 predicates:  
 \- Path=/transfers/\*\*  
 filters:  
 \- name: RequestRateLimiter  
 args:  
 redis-rate-limiter.replenishRate: 10  
 redis-rate-limiter.burstCapacity: 20  
 \- name: CircuitBreaker  
 args:  
 name: paymentCB  
 fallbackUri: forward:/fallback/payment  
 \- RemoveRequestHeader=Cookie  
 \- SaveSession

## **8\. Implementation Checklist**

1. $$ $$  
   Setup HTTPS with SSL certificates (Self-signed for Dev, CA for Prod).
2. $$ $$  
   Configure Redis for Rate Limiter.
3. $$ $$  
   Write GlobalFilter to validate JWT (using nimbus-jose-jwt library).
4. $$ $$  
   Configure strict CORS, no wildcards.
5. $$ $$  
   Implement Fallback Controller to return standard JSON error when Service is down.
