# Authentication Flow (2FA) — Shared Spec

## Goal
- Define the standard two-step login (2FA) for Internal users.
- Ensure consistent contracts for tokens and error handling across services.

## Step 1: Primary Authentication
- Input: username + password
- Output:
  - If 2FA disabled: Access Token + Refresh Token
  - If 2FA enabled: Issue OTP and return PRE_AUTH_TOKEN (short-lived, ~5 minutes), usable only for /auth/verify-otp

## Step 2: OTP Verification
- Input: PRE_AUTH_TOKEN + otp
- Output: Access Token + Refresh Token

## Contracts
- POST /auth/login
  - Request: { "username": "string", "password": "string" }
  - Response (2FA disabled): { "status":200, "message":"OK", "data": { "accessToken":"...", "refreshToken":"..." } }
  - Response (2FA enabled): { "status":200, "message":"OTP_SENT", "data": { "preAuthToken":"...", "expiresIn":300 } }
- POST /auth/verify-otp
  - Request: { "preAuthToken":"string", "otp":"string" }
  - Response: { "status":200, "message":"OK", "data": { "accessToken":"...", "refreshToken":"..." } }
- POST /auth/refresh
  - Request: { "refreshToken":"string" }
  - Response: { "status":200, "message":"OK", "data": { "accessToken":"..." } }

## Related
- See JWT claims: ../auth/jwt_claims.md
- See standard error codes: ../errors/error_codes.md

