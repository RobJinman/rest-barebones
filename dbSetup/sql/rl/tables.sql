CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA rl;

CREATE TABLE IF NOT EXISTS rl.account (
  account_id UUID PRIMARY KEY,
	email VARCHAR(100) UNIQUE NOT NULL,
  username VARCHAR(30) UNIQUE NOT NULL,
  hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS rl.pending_account (
  pending_id SERIAL PRIMARY KEY,
  code VARCHAR(32) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,

  -- Records will expire after some duration.
  ts TIMESTAMP NOT NULL
);

-- A new salt is created periodically and the oldest removed.
-- There are always exactly 2 records in this table.
CREATE TABLE IF NOT EXISTS rl.salt (
  salt TEXT UNIQUE NOT NULL,
  ts TIMESTAMP NOT NULL
);
