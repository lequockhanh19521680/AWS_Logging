# Business Rules (Authentication & Account Security)

## 2FA Policy

- 2FA is mandatory for Internal users (Operator, Checker, Auditor, SysAdmin).
- PRE_AUTH_TOKEN has short TTL (~5 minutes) and can only be used for `/auth/verify-otp`.
- Each login issues a new 6-digit OTP; one-time valid.

## Account Locking

- Auto-lock after N consecutive failed attempts (e.g., 5).
- Unlock via operational workflow (Auditor/Compliance approval).
- Log details: userId, IP, timestamp, reason.

## Privileges & Access

- Enforce Maker-Checker for sensitive transactions.
- Rule: `creatorId` must differ from `checkerId`; require `tx:approve` permission.
- Mask sensitive data by role (e.g., System Admin sees obfuscated balances).

## Tokens & Sessions

- Refresh token has longer TTL than Access; provide revoke/logout endpoint.
- Blacklist `jti` on Redis; logout/regenerate adds to blacklist.
- Disallow tokens from invalid origins/domains (strict CORS).
