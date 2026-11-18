-- authentication-services: sign in with email, single "name" instead of username/first/last name.
-- Run ONCE on database db-acl BEFORE starting the new authentication-services version
-- (the schema is not auto-updated: spring.jpa.hibernate.ddl-auto = none).
--   psql -U postgres -d db-acl -f 2026-11-18_email-login-single-name.sql
-- Runs in one transaction: if any step fails, nothing is changed.

BEGIN;

-- 1. Every user must have a unique, non-empty email, because email is now the login.
--    If this raises an error, fix the listed rows first, then run the script again.
DO $$
DECLARE problems TEXT;
BEGIN
    SELECT string_agg(DISTINCT coalesce(lower(trim(email)), '<empty>'), ', ') INTO problems
    FROM users u
    WHERE coalesce(trim(u.email), '') = ''
       OR (SELECT count(*) FROM users o WHERE lower(trim(o.email)) = lower(trim(u.email))) > 1;
    IF problems IS NOT NULL THEN
        RAISE EXCEPTION 'Users with empty or duplicate (case-insensitive) email: %', problems;
    END IF;
END $$;

-- 2. Store emails trimmed and lower-case (login matching is case-insensitive).
UPDATE users SET email = lower(trim(email));

-- 3. Single name column, filled from first + last name (falls back to the old username).
ALTER TABLE users ADD COLUMN name VARCHAR(100);
UPDATE users
SET name = left(coalesce(nullif(trim(concat_ws(' ', trim(first_name), trim(last_name))), ''), username), 100);
ALTER TABLE users ALTER COLUMN name SET NOT NULL;

-- 4. Remove the old columns (dropping username also drops its unique constraint).
ALTER TABLE users DROP COLUMN username;
ALTER TABLE users DROP COLUMN first_name;
ALTER TABLE users DROP COLUMN last_name;

COMMIT;

-- Note: existing access/refresh tokens contain the old username, so users must sign in again.
