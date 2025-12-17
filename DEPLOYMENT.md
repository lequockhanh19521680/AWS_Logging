# Deployment Guide

## Prerequisites
- Docker & Docker Compose installed
- Java 21 SDK (for local dev)
- Node.js 20+ (for local dev)
- Maven 3.9+

## Build & Run (Docker Compose)

1. **Build Backend Services**
   ```bash
   # From root directory
   # Build Auth Service
   mvn clean package -pl backend/auth-service -am -DskipTests
   # Build Wallet Service
   mvn clean package -pl backend/wallet-service -am -DskipTests
   # Build Transaction Service
   mvn clean package -pl backend/transaction-service -am -DskipTests
   # Build Gateway Service
   mvn clean package -pl backend/gateway-service -am -DskipTests
   ```
   *Note: Dockerfile uses multi-stage build, so you can skip local mvn build if you just run docker-compose build.*

2. **Start Infrastructure & Backend**
   ```bash
   docker-compose up --build -d
   ```
   This starts:
   - Postgres (5432)
   - Kafka (9092) & Zookeeper (2181)
   - Auth Service (8081)
   - Wallet Service (8082)
   - Transaction Service (8083)
   - Gateway Service (8084)

3. **Start Frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   Access at: http://localhost:5173

## Verification Steps

1. **Check Services Health**
   ```bash
   docker-compose ps
   ```
   Ensure all services are "Up".

2. **Register a User**
   - Go to http://localhost:5173/register
   - Sign up with email/password.

3. **Login**
   - Go to http://localhost:5173/login
   - Sign in.
   - You should see the Dashboard.

4. **Perform Topup**
   - Click "Top Up".
   - Enter amount (e.g., 100).
   - Submit.
   - Dashboard balance should update.

## Production Assumptions
- **Database**: Managed PostgreSQL (AWS RDS) with Multi-AZ.
- **Messaging**: Managed Kafka (MSK or Confluent Cloud).
- **Secrets**: Use AWS Secrets Manager or HashiCorp Vault (inject via env vars).
- **Container Orchestration**: Kubernetes (EKS).
- **CI/CD**: Jenkins/GitLab CI pipeline building images and pushing to ECR.
