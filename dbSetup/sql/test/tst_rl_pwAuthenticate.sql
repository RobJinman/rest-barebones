ROLLBACK;
BEGIN;

CREATE TEMP TABLE result (
  item TEXT,
  result TEXT
) ON COMMIT DROP;

INSERT INTO rl.salt
VALUES
  (GEN_SALT('md5'), CURRENT_TIMESTAMP),
  (GEN_SALT('md5'), CURRENT_TIMESTAMP);

CREATE OR REPLACE FUNCTION tst_rl_pwAuthenticate() RETURNS VOID AS $$
DECLARE
  var_addr TEXT;
  var_code TEXT;
  var_pw TEXT;
  var_uname TEXT;
  var_token TEXT;
  var_accId UUID := NULL;

  var_bool BOOLEAN;
BEGIN
  -------------------------------------
  -- TEST CASE 1
  -------------------------------------
  var_addr := 'addressA@example.com';
  var_code := rl.registerUser(var_addr);

  INSERT INTO result
  VALUES ('REGISTER NEW USER');

  -- ASSERTION 1
  INSERT INTO result
    SELECT 'Entry added to pending_account',
      CASE COUNT(*) WHEN 1 THEN 'PASS' ELSE 'FAIL' END
    FROM rl.pending_account
    WHERE email = var_addr AND code = var_code;

  -------------------------------------
  -- TEST CASE 2
  -------------------------------------

  var_uname := 'joe_bloggs';
  var_pw := 'mypassword123';
  var_accId := rl.confirmUser(var_uname, var_pw, var_code);

  INSERT INTO result
  VALUES ('CONFIRM USER');

  -- ASSERTION 1
  INSERT INTO result
    SELECT 'Account ID is returned',
      CASE var_accId IS NOT NULL WHEN TRUE THEN 'PASS' ELSE 'FAIL' END;

  -- ASSERTION 2
  INSERT INTO result
    SELECT 'Entry removed from pending_account',
      CASE COUNT(*) WHEN 0 THEN 'PASS' ELSE 'FAIL' END
    FROM rl.pending_account
    WHERE email = var_addr AND code = var_code;

  -- ASSERTION 3
  INSERT INTO result
    SELECT 'Entry added to account',
      CASE COUNT(*) WHEN 1 THEN 'PASS' ELSE 'FAIL' END
    FROM rl.account
    WHERE email = var_addr AND username = var_uname;

  -- Add another user
  var_addr := 'addressB@mail.com';
  var_uname := 'BenSmith';
  var_pw := 'qwerty';
  var_code := rl.registerUser(var_addr);
  PERFORM rl.confirmUser(var_uname, var_pw, var_code);

  -------------------------------------
  -- TEST CASE 3
  -------------------------------------
  INSERT INTO result
  VALUES ('REGISTER USER WITH REPEAT EMAIL');

  BEGIN
    var_bool := FALSE;

    var_addr := 'addressA@example.com';
    var_code := rl.registerUser(var_addr);
  EXCEPTION
    WHEN SQLSTATE '00001' THEN
      var_bool := TRUE;
  END;

  -- ASSERTION 1
  INSERT INTO result
  VALUES ('Exception thrown', CASE var_bool WHEN TRUE THEN 'PASS' ELSE 'FAIL' END);

  -------------------------------------
  -- TEST CASE 4
  -------------------------------------
  var_token := 'not set';
  var_token := rl.pwAuthenticate(var_uname, var_pw);

  INSERT INTO result
  VALUES ('AUTHENTICATE PW WITH CORRECT CREDENTIALS');

  -- ASSERTION 1
  INSERT INTO result
  VALUES ('Token returned', CASE var_token != 'not set' WHEN TRUE THEN 'PASS' ELSE 'FAIL' END);

  -------------------------------------
  -- TEST CASE 5
  -------------------------------------
  INSERT INTO result
  VALUES ('AUTHENTICATE PW WITH INCORRECT PASSWORD');

  BEGIN
    var_bool := FALSE;
    var_token := rl.pwAuthenticate(var_uname, 'mypasswrd123');
  EXCEPTION
    WHEN SQLSTATE '00002' THEN
      var_bool := TRUE;
  END;

  -- ASSERTION 1
  INSERT INTO result
  VALUES ('Exception thrown', CASE var_bool WHEN TRUE THEN 'PASS' ELSE 'FAIL' END);

  -------------------------------------
  -- TEST CASE 6
  -------------------------------------
  INSERT INTO result
  VALUES ('AUTHENTICATE PW WITH INCORRECT USERNAME');

  BEGIN
    var_bool := FALSE;
    var_token := rl.pwAuthenticate('joe_bleggs', var_pw);
  EXCEPTION
    WHEN SQLSTATE '00001' THEN
      var_bool := TRUE;
  END;

  -- ASSERTION 1
  INSERT INTO result
  VALUES ('Exception thrown', CASE var_bool WHEN TRUE THEN 'PASS' ELSE 'FAIL' END);
END
$$ LANGUAGE plpgsql;

SELECT tst_rl_pwAuthenticate();

SELECT *
FROM result
UNION ALL
SELECT 'RESULT', CASE COUNT(*) WHEN 0 THEN 'PASS' ELSE 'FAIL' END
FROM result
WHERE result = 'FAIL';
