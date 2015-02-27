ROLLBACK;
BEGIN;

CREATE TEMP TABLE result (
  item TEXT,
  pass BOOLEAN
) ON COMMIT DROP;

-- rl.tkAuthenticate() compares the provided token with the result of hashing the username with every salt in the rl.salt table
CREATE OR REPLACE FUNCTION tst_rl_authorise() RETURNS SETOF result AS $$
BEGIN
  CREATE TEMP TABLE testCase (
    description TEXT,
    expected BOOLEAN,
    actual BOOLEAN
  ) ON COMMIT DROP;

  INSERT INTO rl.salt
  VALUES
    ('this_is_a_salt', CURRENT_TIMESTAMP),
    ('the_quick_brown_fox_jumps_over_the_lazy_dog', CURRENT_TIMESTAMP);

  INSERT INTO testCase
    SELECT 'Using existing salt', TRUE, tkAuthenticate
    FROM rl.tkAuthenticate('some_user', HMAC('some_user', 'this_is_a_salt', 'md5'));

  INSERT INTO testCase
    SELECT 'Slightly wrong salt', FALSE, tkAuthenticate
    FROM rl.tkAuthenticate('some_user', HMAC('some_user', 'this_is_a_slt', 'md5'));

  INSERT INTO testCase
    SELECT 'Very wrong salt', FALSE, tkAuthenticate
    FROM rl.tkAuthenticate('some_user', HMAC('some_user', 'this_is_a_different_salt_entirely', 'md5'));

  INSERT INTO testCase
    SELECT 'Using existing salt', TRUE, tkAuthenticate
    FROM rl.tkAuthenticate('joe_bloggs', HMAC('joe_bloggs', 'the_quick_brown_fox_jumps_over_the_lazy_dog', 'md5'));

  INSERT INTO testCase
    SELECT 'Slightly mismatched usernames', FALSE, tkAuthenticate
    FROM rl.tkAuthenticate('joe_bleggs', HMAC('joe_bloggs', 'the_quick_brown_fox_jumps_over_the_lazy_dog', 'md5'));

  RETURN QUERY
    SELECT description, expected = actual
    FROM testCase;
END
$$ LANGUAGE plpgsql;

WITH res AS (
  SELECT *
  FROM tst_rl_authorise()
)
SELECT *
FROM res
UNION ALL
SELECT 'RESULT', COUNT(*) = 0
FROM res
WHERE res.pass = FALSE;
