## Assumptions
- Use three subprojects under `aws-learning-platform/`: `frontend/`, `backend/`, `infrastructure/`.
- Start with Google and GitHub OAuth2; Facebook can be added later.
- Local dev uses Docker for Postgres; prod uses Amazon RDS PostgreSQL.
- JWT is minted by Backend and stored in HttpOnly cookie for SSR safety.

## Repo Setup
- Initialize monorepo folders and baseline READMEs.
- Add shared `.editorconfig`, `.gitignore`, and top-level CI workflow stubs.
- Establish issue templates and environments naming: `dev`, `staging`, `prod`.

## Prompt 1 — Backend Base (Entities + Local DB)
- Create Spring Boot project under `backend/` with modules:
  - `config/`, `controller/`, `entity/`, `repository/`, `service/`, `dto/`, `exception/`.
- Implement JPA + Lombok entities:
  - `User`: `id (UUID)`, `email`, `provider`, `providerId`, `role`, `createdAt`.
  - `Question`: `id`, `title`, `difficultyLevel`, `scenarioDescription`, `thumbnailUrl`.
  - `Step`: `id`, `questionId`, `stepOrder`, `content`, `serviceIcon`.
- Add repositories and skeletal services.
- Configure `application.yml` and `application-dev.yml` (datasource, JPA).
- Provide `docker-compose.yml` at repo root to spin up Postgres locally with network and volume.
- Seed sample data via `data.sql` or simple `CommandLineRunner`.

## Prompt 2 — Auth (Spring Security 6 + OAuth2 + JWT)
- Configure Spring Security 6 (`SecurityConfig`) with OAuth2 Client for Google/GitHub.
- Add `OAuth2LoginSuccessHandler` to upsert `User` on login.
- Implement JWT provider using `Nimbus` or `jjwt` and sign with secret from `AWS Secrets Manager` (fallback `.env` in dev).
- Expose `AuthController` with `POST /api/auth/login/oauth2/code/{registrationId}` and `GET /api/auth/me`.
- Return JWT in HttpOnly cookie and header; implement `JwtAuthenticationFilter` for protected routes.
- Add CORS config and `GlobalExceptionHandler`.
- Optional: integrate AWS Cognito later with resource server validation; keep interfaces modular.

## Prompt 3 — Frontend (Next.js 14 + Tailwind + Axios)
- Scaffold Next.js 14 App Router in `frontend/src/app/` with route groups:
  - `(auth)/login`, `dashboard/`, `questions/`, `questions/[id]/`.
- Add UI components: `DiagramViewer`, `StepList`, `QuestionCard`.
- Install TailwindCSS, configure `tailwind.config.js` and base styles.
- Implement service layer (`src/services/api.ts`) with Axios, interceptors for JWT.
- Store JWT in HttpOnly cookie; use server actions/`fetch` where possible for SSR.
- Build `Login` page calling backend auth, and `Questions` page with pagination.

## Prompt 4 — Infrastructure (CDK: Network + DB)
- Create CDK app in `infrastructure/` with entry `bin/infra.ts` and `lib/stacks/`.
- Implement `NetworkStack` for 3-tier VPC:
  - Public (ALB, NAT), Private (App), Isolated (DB), routing and SGs.
- Implement `DatabaseStack` for Amazon RDS PostgreSQL with parameter group, subnet group, SG allowing from App SG.
- Add `lib/config/env-config.ts` describing per-environment sizes and settings.
- Export stack outputs for connection strings and SG IDs.

## Prompt 5 — Pipeline (CDK Pipelines + CodeBuild + ECR/ECS)
- Create `PipelineStack` using CDK Pipelines self-mutating.
- Stages per env: `Dev`, `Staging`, `Prod` with manual approval in `Prod`.
- CodeBuild steps:
  - Build Backend: Maven package, Docker build, push to ECR.
  - Build Frontend: Next build; either Amplify deploy or ECS image push.
  - Synth CDK.
- Post-build deploy:
  - Update ECS Fargate services with new image via `CodeDeploy`/rolling update.
- Wire artifacts, IAM roles, and secrets handling.

## Backend Runtime (ECS Fargate + ALB)
- Define `BackendStack` creating ECR repo, ECS cluster, Fargate service, ALB, target groups, health checks.
- Environment variables pulled from `SSM Parameter Store`/`Secrets Manager`.

## Frontend Runtime (Amplify or ECS + CloudFront)
- Option 1: Amplify Hosting connected to repo branch.
- Option 2: ECS Fargate service for SSR with ALB; fronted by CloudFront distribution.
- Select Option 2 for consistency and SSR; keep Option 1 path documented.

## Config & Secrets
- Local dev: `.env` files; never commit secrets.
- Cloud: use `AWS Secrets Manager` and `SSM` for DB creds and JWT secret.
- CI: use OpenID Connect to access AWS without long-lived keys.

## API & Docs
- Add Swagger/OpenAPI via Springdoc (`/swagger-ui.html`).
- Version APIs under `/api/*`; implement pagination for `/api/questions`.

## Data Migrations
- Add Flyway/Liquibase for schema migrations; maintain scripts in `backend/src/main/resources/db/migration`.

## Testing & Quality
- Backend: unit tests for services, integration tests for repositories and auth.
- Frontend: component tests with `@testing-library/react` and Playwright e2e for auth and questions.
- Infrastructure: synth and diff tests; smoke tests post-deploy.
- Enable linting/formatting in both projects.

## Observability & Logging
- Use Spring Boot Actuator, structured JSON logs.
- Send logs to CloudWatch; set retention per env.
- Add basic metrics and health checks for ECS/ALB.

## Deliverables per Prompt
- P1: Entities, repositories, configs, `docker-compose.yml`, seed data.
- P2: Security config, OAuth2 login flow, JWT issuance, auth endpoints.
- P3: Next.js app structure, Tailwind setup, Axios services, auth + questions pages.
- P4: CDK `NetworkStack`, `DatabaseStack`, `env-config.ts`.
- P5: `PipelineStack` with build/deploy steps, ECR/ECS wiring.

## Next Action
- On confirmation, start with Prompt 1 and implement the backend entities and local Postgres setup, then verify via simple API and seed reads.