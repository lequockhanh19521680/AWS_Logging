# Database Design (RBAC & Auth)

## Table `users`

- `id` UUID (PK)
- `username` VARCHAR
- `user_type` ENUM [INTERNAL, EXTERNAL]
- `status` ENUM [ACTIVE, LOCKED, PENDING_SETUP]
- `password_hash` VARCHAR (BCrypt, Internal only)
- `two_factor_enabled` BOOLEAN (mandatory for sensitive Internal roles)

## Table `roles`

- `id` UUID (PK)
- `name` VARCHAR (e.g., ROLE_SYS_ADMIN, ROLE_OPERATOR, ROLE_CHECKER)
- `description` VARCHAR

## Table `permissions`

- `id` UUID (PK)
- `resource_code` VARCHAR (e.g., user, wallet, tx)
- `action_code` VARCHAR (e.g., create, approve, view_balance)
- `permission_key` VARCHAR (unique, e.g., tx:create)

## Relationships

- `users` <-> `roles` (N-N)
- `roles` <-> `permissions` (N-N)

## Implementation Notes

- Use Flyway for schema migration.
- Use MapStruct for Entity <-> DTO mapping.
