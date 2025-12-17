This repository follows a strict FinTech architecture.

The source of truth is:
.trae/architecture.md
(FinTech Detail Design Document – Reusable Enterprise Template)

You MUST follow these non-negotiable rules:

ARCHITECTURE:

- Ledger-first for all monetary data
- Wallet/Ledger Service is the single source of truth for balances
- No service may access another service’s database
- Transaction Service orchestrates only, never mutates balance
- No event may be published before DB transaction commit
- All external APIs must be idempotent

CODING:

- Clean Architecture
- No business logic in controllers
- Service layer owns transaction boundaries
- Repository layer = data access only

MESSAGING:

- Kafka = immutable domain events only
- Async processing must be retry-safe

If a request violates these rules:

- You must explain why
- You must propose a compliant alternative
