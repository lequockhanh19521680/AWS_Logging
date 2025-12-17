# Production Readiness Checklist

## Security
- [ ] **JWT Secrets**: Rotated and injected via secure vault.
- [ ] **TLS/SSL**: Enabled for all external and internal traffic (Service Mesh).
- [ ] **Database Access**: Restricted via Security Groups / Network Policies.
- [ ] **Input Validation**: Verified at API Gateway and Service level.

## Reliability
- [ ] **Health Checks**: Liveness and Readiness probes configured.
- [ ] **Retries**: Retry policies configured for inter-service communication (Kafka/HTTP).
- [ ] **Circuit Breakers**: Enabled for external dependencies.
- [ ] **Dead Letter Queues**: Configured for Kafka consumers.

## Data Integrity (FinTech Critical)
- [ ] **Ledger Verification**: Automated reconciliation jobs scheduled.
- [ ] **Backups**: Point-in-time recovery (PITR) enabled for Postgres.
- [ ] **Idempotency**: Verified for all financial transactions.
- [ ] **Audit Logs**: Enabled for all sensitive operations (Wallet/Auth).

## Observability
- [ ] **Logging**: Centralized logging (ELK/Splunk).
- [ ] **Metrics**: Prometheus/Grafana dashboards for business metrics (Tx count, Volume).
- [ ] **Tracing**: Distributed tracing (Jaeger/Zipkin) enabled.
