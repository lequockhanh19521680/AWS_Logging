# **FINTECH MONOREPO STRUCTURE BLUEPRINT (AWS & MICROSERVICES)**

Version: 2.0 (High Security & Scalability)  
Architecture Style: Microservices (Spring Boot) \+ SPA (Angular) \+ Event-Driven  
Infrastructure: AWS (EKS/ECS, RDS, ElastiCache, MSK/SQS)  
**⚠️ AI DIRECTIVE / META-INSTRUCTIONS (CRITICAL)**

To all AI Agents (Trae, Cursor, Copilot, etc.):  
When generating code or scaffolding new modules for this project, you MUST strictly adhere to the structure and architectural patterns defined in this document.

1. **Monorepo Mandate:** Do not suggest creating separate repositories. All new services must reside in services/, and shared libraries in shared-libs/.
2. **Architecture Enforcement:**
   - **Backend:** MUST follow **Hexagonal/Clean Architecture**. Business logic must be isolated from infrastructure and application layers.
   - **Frontend:** MUST follow **Feature-Sliced Design**. Group files by business feature, not by technical type.
3. **Security First:** Always check for hardcoded secrets. Suggest using AWS Secrets Manager placeholders instead.
4. **Deviation Policy:** Any deviation from this structure requires an explicit justification in an ADR (Architecture Decision Record).

## **1\. Overall Structure (Root Level)**

We utilize a **Monorepo** strategy to manage the entire system within a single repository. This facilitates synchronized CI/CD processes and seamless resource sharing across the organization.

fintech-platform/  
├── .github/ \# CI/CD Workflows (GitHub Actions)  
│ ├── workflows/  
│ │ ├── ci-backend.yml \# Build, Test, SonarQube scan  
│ │ ├── ci-frontend.yml \# Build Angular, Lint  
│ │ ├── cd-deploy-dev.yml \# Deploy to AWS Dev env  
│ │ └── security-scan.yml \# OWASP ZAP, TruffleHog (Secret scanning)  
│ └── actions/ \# Custom Composite Actions  
│  
├── .vscode/ \# Shared Team Settings (Extensions, Formatting rules)  
├── docs/ \# Documentation Center  
│ ├── adr/ \# Architecture Decision Records (Why we chose A over B)  
│ ├── api-specs/ \# OpenAPI/Swagger files (Contract First Design)  
│ ├── ai-context/ \# Context for AI Agents (Business Rules, DB Schema)  
│ └── security/ \# Security Policies, Threat Models  
│  
├── infrastructure/ \# Infrastructure as Code (AWS SA Pro Area)  
│ ├── docker/ \# Local Dev Environment (Docker Compose)  
│ ├── k8s/ \# Kubernetes Manifests / Helm Charts  
│ │ ├── apps/ \# Values/Configs for specific microservices  
│ │ └── core/ \# Ingress, Cert-Manager, Monitoring (Prometheus/Grafana)  
│ └── terraform/ \# AWS Infrastructure Provisioning  
│ ├── modules/ \# Reusable modules (VPC, EKS, RDS)  
│ └── environments/ \# State files for dev, staging, prod  
│  
├── shared-libs/ \# Shared Libraries (DRY Principle)  
│ ├── java-common/ \# Shared DTOs, Utils, Security Filters, Exception Handling  
│ └── angular-ui-kit/ \# Shared Components, Pipes, Directives (NPM Workspace)  
│  
├── services/ \# Backend Microservices (Spring Boot)  
│ ├── api-gateway/ \# Spring Cloud Gateway (Entry Point, Rate Limiting)  
│ ├── identity-service/ \# Authentication, Authorization (OAuth2, OIDC)  
│ ├── core-banking-service/ \# Account Management, General Ledger  
│ ├── payment-service/ \# Transaction Processing, Payment Gateway Integration  
│ ├── audit-log-service/ \# Security Auditing (Immutable Logs) \- CRITICAL  
│ └── notification-service/ \# Email, SMS, Push Noti (Async via Kafka/SQS)  
│  
└── clients/ \# Frontend Applications (Angular)  
 ├── web-portal/ \# Internet Banking for Retail Customers  
 └── back-office/ \# Admin Dashboard for Bank Staff

## **2\. Backend Details (Spring Boot Microservice)**

Each service within the services/ directory must strictly follow **Hexagonal Architecture** (or Clean Architecture) to ensure the Core Business Logic remains independent of Frameworks, APIs, or Databases.

**Example:** services/payment-service/

payment-service/  
├── src/  
│ ├── main/  
│ │ ├── java/com/khanh/fintech/payment/  
│ │ │ ├── config/ \# Configuration Beans (Security, Swagger, Kafka)  
│ │ │ │  
│ │ │ ├── application/ \# Application Layer (Use Cases/Orchestration)  
│ │ │ │ ├── ports/ \# Interfaces (Input/Output ports)  
│ │ │ │ └── service/ \# Implementation of Business Use Cases  
│ │ │ │  
│ │ │ ├── domain/ \# Domain Layer (Enterprise Rules \- Pure Java)  
│ │ │ │ ├── model/ \# Domain Entities (Rich Models, NOT JPA Entities)  
│ │ │ │ └── exception/ \# Domain Specific Exceptions  
│ │ │ │  
│ │ │ ├── infrastructure/ \# Infrastructure Layer (Adapters)  
│ │ │ │ ├── inbound/ \# Driving Adapters (Controller, Kafka Consumer)  
│ │ │ │ │ ├── rest/ \# REST Controllers  
│ │ │ │ │ └── dto/ \# Request/Response DTOs  
│ │ │ │ │  
│ │ │ │ └── outbound/ \# Driven Adapters (Repository, Feign Client)  
│ │ │ │ ├── persistence/ \# JPA/MyBatis Repositories & Entities  
│ │ │ │ └── external/ \# Clients for Core Banking or Partner APIs  
│ │ │ │  
│ │ │ └── PaymentServiceApplication.java  
│ │ │  
│ │ └── resources/  
│ │ ├── application.yaml \# Default Configuration  
│ │ ├── application-prod.yaml \# Prod Config (References AWS Secrets Manager)  
│ │ ├── db/migration/ \# Flyway scripts (V1\_\_init.sql)  
│ │ └── logback-spring.xml \# Log Config (JSON format for CloudWatch)  
│ │  
│ └── test/ \# Unit Tests & Integration Tests (Testcontainers)  
│  
├── build.gradle \# Service-specific dependencies  
└── Dockerfile \# Multi-stage build (Security optimized)

## **3\. Frontend Details (Angular Enterprise)**

Apply **Feature-Sliced Design** or **Modular Architecture** to effectively manage the complexity of the Fintech application.

**Example:** clients/web-portal/

src/  
├── app/  
│ ├── core/ \# Singleton Services (Loaded once)  
│ │ ├── auth/ \# AuthService, AuthGuard, Interceptors (JWT)  
│ │ ├── config/ \# AppConfig (Runtime JSON loading)  
│ │ ├── error-handling/ \# Global Error Handler  
│ │ └── security/ \# CSRF protection, Encryption utils  
│ │  
│ ├── shared/ \# Dumb Components (Reusable)  
│ │ ├── components/ \# Button, InputMoney, DatePicker  
│ │ ├── pipes/ \# CurrencyFormat, AccountMasking (\*\*\*\*1234)  
│ │ └── directives/ \# PermissionCheck (Hide element if unauthorized)  
│ │  
│ ├── features/ \# Business Modules (Lazy Loaded)  
│ │ ├── dashboard/  
│ │ ├── transfers/ \# Money Transfer Module  
│ │ │ ├── components/ \# Smart Components (TransferForm)  
│ │ │ ├── services/ \# TransferService (API Calls)  
│ │ │ ├── store/ \# State Management (NgRx SignalStore)  
│ │ │ └── models/  
│ │ └── accounts/ \# Account Management Module  
│ │  
│ ├── layouts/ \# Page Structures  
│ │ ├── auth-layout/ \# Layout for Login/Register pages  
│ │ └── main-layout/ \# Main Layout (Sidebar, Header)  
│ │  
│ ├── app.config.ts \# Provider Configuration (Angular Standalone)  
│ └── app.routes.ts \# Root Routing Definitions  
│  
└── environments/ \# Environment variables

## **4\. Security & Compliance Checklist (For Fintech)**

When building upon this structure, the following security measures are **MANDATORY**:

1. **Secret Management:**
   - **NEVER** commit hard-coded passwords or API Keys to Git (especially in .yaml files).
   - Use **AWS Secrets Manager** or **HashiCorp Vault**. Spring Boot applications must load secrets at runtime.
2. **Audit Logging (via audit-log-service):**
   - Every data mutation (Create, Update, Delete) **MUST** be logged.
   - Log entry format: Who (User ID), When (Timestamp), What (Action), Where (IP), Old Value, New Value.
   - Logs must be shipped to **AWS CloudWatch Logs** or **S3 (WORM model \- Write Once Read Many)** to ensure immutability.
3. **Authentication & Authorization:**
   - Use a centralized **Identity Service** (e.g., Keycloak or AWS Cognito).
   - Inter-service communication must use **mTLS** (Mutual TLS) or internal JWT propagation.
4. **Dependency Scanning:**
   - The .github/workflows/security-scan.yml pipeline must run daily to detect vulnerabilities (Snyk/OWASP Dependency Check).

## **5\. References for AWS SA Pro Study**

- **Infrastructure:** Deep dive into terraform/modules/vpc (Networking, Private/Public Subnets) and terraform/modules/eks (Kubernetes Security Groups).
- **Resilience:** Design all services to be Multi-AZ (Availability Zones) compliant.
- **Observability:** Implement **AWS X-Ray** or **OpenTelemetry** (embedded in java-common) for distributed tracing across microservices.
