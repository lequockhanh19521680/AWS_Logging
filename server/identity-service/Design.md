# **Detail Design: Identity Service (Fintech Backend)**

## **1\. Overview**

The identity-service is the security heart of the system, responsible for identity management, authentication, and authorization.

**Design Highlights:**

- **Strict RBAC (Role-Based Access Control):** Ensures granular access control.
- **Clear Separation:** Distinct separation between **Internal Users** (Employees) and **External Users** (Partner Systems/Merchants).
- **Maker-Checker Mechanism:** Supports the "Maker-Checker" (Creator \- Approver) workflow to prevent internal fraud.

## **2\. Actor & Authorization Design (Advanced RBAC Design)**

The system does not use a generic "Admin" role but divides permissions strictly according to financial operations.

### **2.1. User Classification (Actor Definition)**

**A. Internal Users (Operations Staff)**

- **System Admin:** Responsible for configuration and creating employee accounts. _Absolutely not allowed to interfere with balances or create financial transactions._
- **Operator / Accountant:** Performs daily operations (manual credits, handling disputes).
- **Auditor / Compliance:** Read-only access to check logs and detect fraud.

**B. External Users (Machine Users)**

- **Merchant / API Clients:** Service accounts representing partners (e.g., Tiki, Shopee) calling payment APIs. Authenticated via Client Credentials (Client ID \+ Secret) or API Key.

### **2.2. Permission Matrix**

Permissions are granularly defined in the format resource:action.

| Resource             | Action (Permission)         | System Admin             | Operator (Accountant)  | Auditor (Compliance) | Merchant (API)       |
| :------------------- | :-------------------------- | :----------------------- | :--------------------- | :------------------- | :------------------- |
| **User Management**  | user:create, user:block     | ✅ (Full)                | ❌                     | ❌                   | ❌                   |
| **Wallet / Account** | wallet:view_balance         | ❌ (Mask sensitive data) | ✅                     | ✅                   | ✅ (Own wallet only) |
|                      | wallet:freeze               | ✅                       | ✅                     | ❌                   | ❌                   |
| **Transaction**      | tx:create                   | ❌                       | ✅ (Requires approval) | ❌                   | ✅                   |
|                      | tx:approve                  | ❌                       | ❌ (_Checker role_)    | ❌                   | ❌                   |
| **Audit Logs**       | log:view                    | ✅ (System log)          | ❌                     | ✅ (Financial log)   | ❌                   |
| **Configuration**    | config:update (Rates, fees) | ❌                       | ✅                     | ❌                   | ❌                   |

## **3\. Database Schema Design**

Table structure supporting Dynamic RBAC.

### **Table users**

| Field              | Type    | Description                              |
| :----------------- | :------ | :--------------------------------------- |
| id                 | UUID    | PK                                       |
| username           | VARCHAR | Login username                           |
| user_type          | ENUM    | INTERNAL (Employee), EXTERNAL (Merchant) |
| status             | ENUM    | ACTIVE, LOCKED, PENDING_SETUP            |
| password_hash      | VARCHAR | BCrypt hash (Internal users only)        |
| two_factor_enabled | BOOLEAN | Mandatory for critical Internal Users    |

### **Table roles**

| Field       | Type    | Description                                       |
| :---------- | :------ | :------------------------------------------------ |
| id          | UUID    | PK                                                |
| name        | VARCHAR | E.g., ROLE_SYS_ADMIN, ROLE_OPERATOR, ROLE_CHECKER |
| description | VARCHAR | Role description                                  |

### **Table permissions**

| Field          | Type    | Description                         |
| :------------- | :------ | :---------------------------------- |
| id             | UUID    | PK                                  |
| resource_code  | VARCHAR | E.g., user, wallet, tx              |
| action_code    | VARCHAR | E.g., create, approve, view_balance |
| permission_key | VARCHAR | Unique Key: tx:create, tx:approve   |

- **Relationships:** users \<-\> roles (N-N), roles \<-\> permissions (N-N).

## **4\. Special Business Logic Flow**

### **4.1. Maker-Checker Mechanism (Transaction Approval Process)**

This is the most critical feature to ensure financial safety and prevent employees from arbitrarily transferring money.

**Scenario:** Operator A wants to manually credit (Deposit) money to a customer's wallet.

1. **Phase 1: Maker (Creator)**
   - **User:** Operator A (has tx:create permission).
   - **Action:** Calls API POST /transactions/deposit.
   - **System:**
     - Checks tx:create permission.
     - Creates transaction with status **PENDING_APPROVAL**.
     - Logs audit: "Operator A created deposit request".
   - **Result:** Money is **not yet** credited to the customer's wallet.
2. **Phase 2: Checker (Approver)**
   - **User:** Manager B (has tx:approve permission).
   - **Logic:** System enforces **Maker ID \!= Checker ID** (Creator cannot approve their own request).
   - **Action:** Calls API POST /transactions/{id}/approve.
   - **System:**
     - Checks tx:approve permission.
     - Checks logic current_user.id \!= transaction.creator_id.
     - Updates transaction status to **SUCCESS**.
     - Calls _Core Banking Service_ to credit the wallet.
   - **Result:** Money is credited to the customer's wallet.

### **4.2. Authentication Flow (Updated: 2-Step 2FA)**

To ensure maximum security for Internal Users, the Login process is split into two steps using an intermediate token.

See shared references:
- Authentication Flow: `../../docs/shared_list/auth/authentication.md`
- JWT Claims Contract: `../../docs/shared_list/auth/jwt_claims.md`
- Error Codes: `../../docs/shared_list/errors/error_codes.md`
**Step 1: Primary Authentication**

- **Input:** Username \+ Password.
- **Process:** Verify credentials and user status (ACTIVE).
- **Output:**
  - If 2FA is **disabled**: Return Access Token \+ Refresh Token immediately.
  - If 2FA is **enabled**: Generate OTP (send via Email/SMS) and return a **PRE_AUTH_TOKEN** (Temporary Token).
  - _Constraint:_ PRE_AUTH_TOKEN is short-lived (e.g., 5 minutes) and has **NO** permission to access resources. It can only call the /verify-otp endpoint.

**Step 2: OTP Verification**

- **Input:** PRE_AUTH_TOKEN \+ OTP Code.
- **Process:** Verify the temporary token signature and check if the OTP matches/is valid.
- **Output:** Return standard Access Token \+ Refresh Token.

### **4.3. Standard JWT Structure (Claims Contract)**

To ensure the API Gateway can correctly extract user information and forward it to downstream services (via X-User-Id and X-Roles headers), the JWT Payload must strictly follow this structure:

{  
 "sub": "a1b2c3d4-...", // User UUID (Mapped to X-User-Id)  
 "iss": "fintech-identity", // Issuer  
 "iat": 1678900000, // Issued At  
 "exp": 1678903600, // Expiration  
 "type": "ACCESS", // Token Type: ACCESS or REFRESH  
 "roles": \[ // Mapped to X-Roles header  
 "ROLE_OPERATOR",  
 "ROLE_CHECKER"  
 \],  
 "permissions": \[ // Optional: for fine-grained checks if needed  
 "tx:create",  
 "wallet:view"  
 \]  
}

## **5\. Security Checklist**

- **Principle of Least Privilege:** Default Deny All, grant only minimum necessary permissions.
- **Sensitive Data Masking:** API returning wallet/balance info for System Admin must mask figures (e.g., \*\*\*\*\*).
- **Audit Logging:** All actions (create, approve, reject) must record IP, User ID, Timestamp, and old/new data snapshots.
- **Internal Network:** Critical APIs (like approve) should only be callable from the internal network or VPN.
