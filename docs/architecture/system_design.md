# System Design: Authentication Flow

## Sequence (High-Level)
1. Client calls `POST /auth/login` → Gateway.
2. Gateway checks rate limit, validates JWT if present, sanitizes headers → forwards to Identity.
3. Identity:
   - If 2FA disabled: returns Access + Refresh.
   - If 2FA enabled: generates OTP, returns `PRE_AUTH_TOKEN`.
4. Client calls `POST /auth/verify-otp` with `PRE_AUTH_TOKEN` + `otp` → Gateway → Identity.
5. Identity returns Access + Refresh; Gateway injects `X-User-Id`, `X-Roles` for subsequent secured requests.
6. Downstream services (payment/core-banking/notification) use headers/claims for authorization.

## Notes
- `/auth/**` is public via Gateway; other APIs require a valid JWT.
- JWT must follow the shared claims contract.
- See shared spec: `../shared_list/auth/authentication.md` and `../shared_list/auth/jwt_claims.md`.
