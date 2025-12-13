## Goals
- Build scalable microservices for payments, loyalty, wallet, orders, inventory, notifications.
- Guarantee financial integrity via double-entry ledger, idempotency, and Saga orchestration.
- Achieve low-latency operations with Redis locks and robust Kafka-based eventing.
- Deliver automated CI/CD to AWS ECS Fargate with observability and security.

## Architecture
- Microservices: Payment, Loyalty, Wallet, Order (Saga Orchestrator), Inventory, Notification.
- Communication: REST (sync) + Kafka (async). Use Outbox pattern for reliable publishing.
- Clean Architecture per service: domain, application, infrastructure, adapters.
- Persistence: PostgreSQL per service; Redis for locks and caches; MongoDB for notifications.
- Service discovery: AWS Cloud Map with private DNS; ALB as public ingress.

## Domain & Data Models
- Wallet/Loyalty Ledger:
  - `accounts(id, type, balance)`; check constraints for `type`.
  - `journal_entries(id, transaction_ref, posted_at)`; unique `transaction_ref` for idempotency.
  - `ledger_lines(entry_id, account_id, amount, direction)`; check `amount > 0`, `direction ∈ {DEBIT,CREDIT}`.
  - DB invariants: trigger/function to enforce `sum(debits) = sum(credits)` per `entry_id`.
- Loyalty FIFO Lots:
  - `point_lots(id, user_id, current_amount, expiry_date)`; index by `user_id, expiry_date`.
  - `point_consumptions(lot_id, txn_id, amount)` for audit of redemptions.
- Orders & Inventory:
  - Orders: `orders(id, state, total_amount, currency)`; state machine transitions.
  - Inventory: `skus(id, stock)`; `reservations(order_id, sku_id, qty, state)`.
- Idempotency:
  - `processed_requests(idempotency_key, service, response_hash, created_at)` with unique constraint.

## Event Contracts (Kafka)
- Topics: `inventory.reserve`, `inventory.reserved`, `inventory.release`; `payment.process`, `payment.authorized`, `payment.refunded`; `loyalty.accrue`, `loyalty.accrued`, `loyalty.failed`; `order.completed`, `order.cancelled`.
- Payloads include `eventId`, `eventType`, `aggregateId`, `sagaId`, `timestamp`, `payload`.
- Versioned schemas via JSON Schema; registry for compatibility checks.

## Saga Orchestration (Order Service)
- Happy Path: Create order(PENDING) → Reserve stock → Process payment (split) → Accrue points → Confirm order.
- Compensation: On failure of any step → refund payments, release stock, mark CANCELLED.
- Orchestrator state persisted: `sagas(id, order_id, state, step_pointer)`.
- Timeouts & retries: exponential backoff; DLQ for poison messages; idempotent handlers.

## Concurrency & Idempotency
- Redis locks with Redisson:
  - Keys: `lock:inventory:sku:{id}` and `lock:wallet:{user_id}`.
  - Use `tryLock(wait=5s, lease=10s)`; ensure unlock in finally.
- Idempotency-Key header validated at adapters; check table before execution; return cached response.
- Prevent double-spend by re-checking ledger/account derived balances within lock.

## Payment Split & Refund Rules
- Split calculator in application layer: resolves sources by priority and limits.
- Atomicity via Saga: partial failures trigger compensations.
- Source-based refund: proportional by contribution unless overridden business rule.

## APIs & DTOs
- REST endpoints per service:
  - Payment: `POST /payments/process`, `POST /payments/refund`.
  - Wallet: `POST /wallets/{userId}/deposit|withdraw`, `GET /wallets/{userId}`.
  - Loyalty: `POST /loyalty/accrue`, `POST /loyalty/redeem`, `GET /loyalty/{userId}/balance`.
  - Order: `POST /orders`, `GET /orders/{id}`; orchestrates.
  - Inventory: `POST /inventory/reserve`, `POST /inventory/release`.
- DTO mapping layer; immutable value objects (`Money`, `Points`).

## Application & Infrastructure Implementation
- Domain: pure Java entities, services, interfaces; no framework annotations.
- Application: use cases and transactions; orchestrate workflow boundaries.
- Infrastructure: adapters for JPA/Hibernate, Kafka producers/consumers, external gateways (e.g., Stripe).
- Persistence: repositories implement outbound ports; transactional outbox tables per service.

## Double-Entry Enforcement
- DB triggers/functions:
  - On `ledger_lines` insert/update: accumulate per `entry_id` and enforce equality of debits/credits.
  - Optional: materialized view for `account_balances` updated by journal postings.
- Derived balance only: forbid direct `UPDATE balance = balance + ...` outside posting function.

## Loyalty FIFO Consumption
- Redemption algorithm:
  - Query `point_lots` by `user_id` ordered by `expiry_date`;
  - Consume across lots until requested amount satisfied; create `consumptions` rows;
  - Update `current_amount` atomically within Redis lock.
- Expiration job:
  - Scheduled batch marks expired lots and posts ledger adjustments (expense/liability).

## Reliability Patterns
- Outbox + Kafka publisher: transactional write, background dispatcher publishes and marks sent.
- Circuit breakers/timeouts for external payment gateways.
- Retries with jitter; DLQ topics and compensating actions.

## AWS Infrastructure (IaC)
- VPC with public (ALB+NAT) and private subnets (ECS tasks, RDS, Redis).
- RDS PostgreSQL per service; ElastiCache Redis; MongoDB Atlas or AWS DocumentDB for notifications.
- ECS Fargate tasks per service; task definitions with sidecars (OTEL collector).
- Service discovery via Cloud Map; internal service DNS (e.g., `http://payment.local:8080`).
- Secrets: AWS Secrets Manager; KMS for encryption.

## CI/CD Pipeline
- CI (GitHub Actions): checkout → unit tests (JUnit 5) → static analysis (SonarCloud) → build → Docker image → push to ECR.
- CD: update ECS task definitions with new image tag; rolling update; health checks.
- Promotion: staged environments (dev → staging → prod) with manual approval gates.

## Observability
- Logging: JSON structured logs with correlation IDs (`sagaId`, `eventId`).
- Metrics: application KPIs (auth rate, refund rate), RED/USE metrics.
- Tracing: OpenTelemetry; spans across services via Kafka headers.
- Dashboards/alerts in CloudWatch/Grafana; SLOs for latency and error budget.

## Testing Strategy
- Unit: BigDecimal precision using AssertJ `isEqualByComparingTo`.
- Integration: Testcontainers for PostgreSQL, Redis; Kafka test harness.
- Contract tests: consumer-driven contracts for REST and events.
- Chaos: inject failures/timeouts to validate compensations.

## Security & Compliance
- Input validation and authN/Z with JWT/OAuth2.
- PCI-lite considerations: never store raw PAN; tokenized card via gateway.
- Least privilege IAM; VPC-only data stores; TLS everywhere.

## Rollout & Migration
- Seed base accounts and chart-of-accounts.
- Blue/green or canary deployments; feature flags for split payments.
- Data migration scripts for ledger and loyalty lot structures.

## Deliverables
- Service repositories with Clean Architecture skeletons.
- Schema migration scripts (Liquibase/Flyway).
- Kafka topic definitions and schemas.
- IaC templates for AWS (CloudFormation/Terraform).
- CI/CD pipelines and operational dashboards.

## Next Steps
- Scaffold services and shared libraries; implement ledger core first.
- Integrate Outbox + Kafka; build Order Saga orchestrator.
- Add Redis lock and idempotency mechanisms; implement split payment flows.
- Deploy to AWS dev environment and validate end-to-end tests.