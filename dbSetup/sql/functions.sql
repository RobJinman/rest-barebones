--CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-------------------------------------------------------------------
-- rl.registerUser
--
-- PARAMS
-- arg_mail: The user's email address
--
-- RETURNS
-- TEXT. A unique activation code
--
-- EXCEPTIONS
-- 00001: There is already an account associated with this email
------------------------------------------------------------------
CREATE OR REPLACE FUNCTION rl.registerUser(arg_mail TEXT) RETURNS TEXT AS $$
DECLARE
  var_code TEXT := MD5(RANDOM()::TEXT);
BEGIN
  IF (
    SELECT COUNT(*) > 0
    FROM rl.account
    WHERE email = arg_mail
  )
  THEN
    RAISE EXCEPTION SQLSTATE '00001';
  END IF;

  -- Delete previous pending accounts associated with this email
  DELETE FROM rl.pending_account
  WHERE email = arg_mail;

  INSERT INTO rl.pending_account (pending_id, code, email, ts)
  VALUES (DEFAULT, var_code, arg_mail, CURRENT_TIMESTAMP);

  RETURN var_code;
END
$$ LANGUAGE plpgsql;

------------------------------------------------------------------
-- rl.confirmUser
--
-- PARAMS
-- arg_uname:  The user's newly chosen username
-- arg_pw:     The user's newly chosen password
-- arg_code:   The verification/activation code the user was emailed
--
-- RETURNS
-- UUID. Returns the accountID if confirmation was successful, NULL otherwise
------------------------------------------------------------------
CREATE OR REPLACE FUNCTION rl.confirmUser(arg_uname TEXT, arg_pw TEXT, arg_code TEXT) RETURNS UUID AS $$
DECLARE
  var_pendingId INTEGER := NULL;
  var_email TEXT := NULL;
  var_hash TEXT := CRYPT(arg_pw, GEN_SALT('bf'));
  var_accId UUID;
BEGIN
  SELECT pending_id, email INTO var_pendingId, var_email
  FROM rl.pending_account
  WHERE code = arg_code;

  IF var_pendingId IS NOT NULL THEN
    INSERT INTO rl.account (account_id, email, username, hash)
    VALUES (UUID_GENERATE_V4(), var_email, arg_uname, var_hash)
    RETURNING account_id INTO var_accId;

    DELETE FROM rl.pending_account
    WHERE pending_id = var_pendingId;

    RETURN var_accId;
  ELSE
    -- Operation failed. No account pending with matching code.
    RETURN NULL;
  END IF;
END
$$ LANGUAGE plpgsql;

------------------------------------------------------------------
-- rl.pwAuthenticate
--
-- PARAMS
-- arg_uname: The user's username
-- arg_pw:    The user's password
--
-- RETURNS
-- TEXT. The authentication token
--
-- EXCEPTIONS
-- 00001: Username not found
-- 00002: Wrong password
------------------------------------------------------------------
CREATE OR REPLACE FUNCTION rl.pwAuthenticate(arg_uname TEXT, arg_pw TEXT) RETURNS BYTEA AS $$
DECLARE
  var_hash TEXT := NULL;
BEGIN
  SELECT hash INTO var_hash
  FROM rl.account
  WHERE username = arg_uname;

  IF var_hash IS NULL THEN
    RAISE EXCEPTION SQLSTATE '00001';
  END IF;

  IF CRYPT(arg_pw, var_hash) != var_hash THEN
    RAISE EXCEPTION SQLSTATE '00002';
  END IF;

  RETURN rl.makeToken(arg_uname);
END
$$ LANGUAGE plpgsql;

------------------------------------------------------------------
-- rl.makeToken
--
-- PARAMS
-- arg_uname: The user's username
--
-- RETURNS
-- BYTEA. An authentication token computed from the username and the most recent global salt
------------------------------------------------------------------
CREATE OR REPLACE FUNCTION rl.makeToken(arg_uname TEXT) RETURNS BYTEA AS $$
DECLARE
  var_token BYTEA;
BEGIN
  -- Hash the username with the latest global salt
  SELECT HMAC(arg_uname, salt, 'md5') INTO var_token
  FROM rl.salt
  ORDER BY rl.salt.ts DESC
  LIMIT 1;

  RETURN var_token;
END
$$ LANGUAGE plpgsql;

------------------------------------------------------------------
-- rl.tkAuthenticate
--
-- PARAMS
-- arg_uname: The user's username
-- arg_token: The authentication token sent with every request
--
-- RETURNS
-- BOOLEAN. Returns TRUE if authorisation is successful, false otherwise
------------------------------------------------------------------
CREATE OR REPLACE FUNCTION rl.tkAuthenticate(arg_uname TEXT, arg_token BYTEA) RETURNS BOOLEAN AS $$
DECLARE
  var_hash BYTEA;
  var_salt TEXT;
BEGIN
  FOR var_salt IN
    SELECT salt
    FROM rl.salt
    ORDER BY rl.salt.ts DESC
  LOOP
    SELECT * INTO var_hash
    FROM HMAC(arg_uname, var_salt, 'md5');

    IF var_hash = arg_token THEN
      RETURN TRUE;
    END IF;
  END LOOP;

  RETURN FALSE;
END
$$ LANGUAGE plpgsql;

------------------------------------------------------------------
-- rl.activateAccount
--
-- PARAMS
-- arg_accountId: The user's account ID
------------------------------------------------------------------
CREATE OR REPLACE FUNCTION rl.activateAccount(arg_accountId UUID) RETURNS VOID AS $$
BEGIN
  UPDATE rl.account
  SET is_active = TRUE
  WHERE account_id = arg_accountId;
END
$$ LANGUAGE plpgsql;
