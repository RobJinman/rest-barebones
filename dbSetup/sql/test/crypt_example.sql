ROLLBACK;
BEGIN;

DO $$
DECLARE
  var_hash TEXT;
BEGIN
  CREATE TEMP TABLE tmp (
    description TEXT,
    result TEXT
  );

  INSERT INTO tmp
  VALUES ('Hash made from new password encrypted with salt', CRYPT('password', GEN_SALT('bf')))
  RETURNING result INTO var_hash;

  INSERT INTO tmp
  VALUES ('Password attempt encrypted with hash. (Should be equal if correct)', CRYPT('password', var_hash));
END
$$;

SELECT *
FROM tmp;