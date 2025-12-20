# JWT Claims — Shared Contract

## Payload Example
{
  "sub": "a1b2c3d4-...", 
  "iss": "fintech-identity",
  "iat": 1678900000,
  "exp": 1678903600,
  "type": "ACCESS",
  "roles": ["ROLE_OPERATOR", "ROLE_CHECKER"],
  "permissions": ["tx:create", "wallet:view"]
}

## Notes
- `sub`: User UUID, forwarded as `X-User-Id`.
- `roles`: Forwarded as `X-Roles`.
- `type`: `ACCESS` or `REFRESH`.
- `permissions`: Optional for fine-grained checks.
