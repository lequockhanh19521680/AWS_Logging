# Enterprise FinTech System Design

## 1. High-Level System Overview
This system is a comprehensive FinTech platform designed for enterprise-grade financial operations. It strictly follows the **Ledger-First** architecture, ensuring that all monetary values are derived from immutable ledger entries.

### Core Capabilities
- **Identity & Access Management**: Secure user onboarding and KYC workflows.
- **Wallet & Ledger**: Double-entry accounting system with strict ACID compliance.
- **Transaction Processing**: State-machine-driven orchestration for Top-ups, Withdrawals, P2P, and Payments.
- **External Integration**: Idempotent integration with payment gateways.
- **Event-Driven Architecture**: Kafka-based asynchronous messaging for side effects (notifications, analytics).

---

## 2. Service & Responsibility Map

| Service | Responsibility | Database | Key Events Published |
|---------|----------------|----------|----------------------|
| **Auth Service** | User Reg, Auth (JWT), KYC Workflow | `db_auth` | `UserCreated`, `KycVerified` |
| **Wallet Service** | **Source of Truth** for Balance. Ledger Entries (Debit/Credit). Freeze/Unfreeze. | `db_wallet` | `WalletCreated`, `BalanceUpdated`, `FundsFrozen` |
| **Transaction Service** | Orchestration, State Machine (CREATED -> PENDING -> COMPLETED/FAILED). | `db_transaction` | `TxCreated`, `TxCompleted`, `TxFailed` |
| **Gateway Service** | External Payment Gateway Integration (Mock). Idempotency. | `db_gateway` | `PaymentProcessed` |
| **Frontend (Web)** | UI/UX, Auth Flow, Dashboard, Form handling. | N/A | N/A |

---

## 3. Backend Design & Implementation

### Technology Stack
- **Framework**: Java 21, Spring Boot 3.2+
- **Architecture**: Clean Architecture (Domain, Application, Infrastructure layers)
- **Database**: PostgreSQL (Schema per service)
- **Messaging**: Apache Kafka
- **Containerization**: Docker

### Domain 1: Identity & Access (Auth Service)
- **API**: `POST /auth/register`, `POST /auth/login`, `POST /kyc/submit`, `GET /kyc/status`
- **Logic**: Users start as `PENDING_KYC`. Admin or mock process approves to `active`.
- **Security**: BCrypt password hashing, JWT generation (RS256).

### Domain 2: Wallet & Ledger (Wallet Service)
- **API**: `POST /wallets`, `GET /wallets/{id}/balance`, `POST /ledger/entry` (Internal only)
- **Ledger Model**:
  - Table `ledger_entries`: `id`, `wallet_id`, `amount`, `currency`, `type` (DEBIT/CREDIT), `reference_id`, `created_at`.
  - Balance is `SUM(amount)` where type adjusts sign.
- **Constraints**: No negative balance (unless overdraft allowed). Strong consistency (SERIALIZABLE or Row Locking).

### Domain 3: Transactions (Transaction Service)
- **API**: `POST /transactions/topup`, `POST /transactions/withdraw`, `POST /transactions/transfer`
- **State Machine**:
  1. `CREATED`: Initial request.
  2. `PENDING`: Wallet reservation (Freeze) or Gateway request.
  3. `CLEARED/SETTLED`: Money moved.
  4. `COMPLETED`: Final state.
  5. `FAILED`: Reversal required if reserved.

### Domain 4: External Integration (Gateway Service)
- **Role**: Simulates Stripe/PayPal.
- **Idempotency**: Uses `Idempotency-Key` header. Stores request hash.

### Domain 5: Events & Messaging
- **Pattern**: Transactional Outbox or "Publish after Commit".
- **Topics**: `user.events`, `wallet.events`, `transaction.events`.

---

## 4. Frontend Design

### Technology Stack
- **Framework**: React (Vite) + TypeScript
- **State**: TanStack Query (Server state), Zustand (Client state)
- **Styling**: Tailwind CSS
- **Components**: Shadcn UI (conceptual)

### Key Screens
1. **Login/Register**: Auth forms.
2. **Dashboard**: Show Wallet Balance (big), Recent Transactions list.
3. **Top-up / Withdraw**: Forms with validation.
4. **KYC Status**: Banner showing current status.

---

## 5. Infrastructure & Deployment

### Architecture
- **Docker Compose**: Orchestrates all services locally.
- **Networking**: Internal bridge network. API Gateway (Nginx or Spring Cloud Gateway) exposes single port.

### Services
- `postgres`: Port 5432 (multi-db).
- `kafka` + `zookeeper`: Messaging.
- `auth-service`: Port 8081.
- `wallet-service`: Port 8082.
- `transaction-service`: Port 8083.
- `gateway-service`: Port 8084.
- `frontend`: Port 3000.

### Environment
- **Local**: `.env` file with default credentials.
- **Production**: Kubernetes manifests (future scope), Secrets management.

