CREATE TABLE accounts (
  id UUID PRIMARY KEY,
  type VARCHAR NOT NULL,
  balance DECIMAL NOT NULL,
  CONSTRAINT accounts_type_chk CHECK (type IN ('ASSET','LIABILITY','EXPENSE'))
);
CREATE TABLE journal_entries (
  id UUID PRIMARY KEY,
  transaction_ref VARCHAR UNIQUE NOT NULL,
  posted_at TIMESTAMP NOT NULL
);
CREATE TABLE ledger_lines (
  entry_id UUID NOT NULL REFERENCES journal_entries(id),
  account_id UUID NOT NULL REFERENCES accounts(id),
  amount DECIMAL NOT NULL,
  direction VARCHAR NOT NULL,
  CONSTRAINT ledger_lines_amount_chk CHECK (amount > 0),
  CONSTRAINT ledger_lines_direction_chk CHECK (direction IN ('DEBIT','CREDIT'))
);
