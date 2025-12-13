CREATE TABLE processed_requests (
  idempotency_key VARCHAR PRIMARY KEY,
  service VARCHAR NOT NULL,
  response_hash VARCHAR NOT NULL,
  created_at TIMESTAMP NOT NULL
);
