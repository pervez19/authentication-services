-- authentication-services: re-activate users registered while account_non_expired, account_non_locked and
-- credentials_non_expired were wrongly saved as false (they could not log in).
-- Only rows with ALL three flags false are changed (the bug's signature), so deliberately locked users are kept.
--   psql -U postgres -d db-acl -f 2026-11-18_activate-new-users.sql

UPDATE users
SET account_non_expired     = true,
    account_non_locked      = true,
    credentials_non_expired = true
WHERE account_non_expired = false
  AND account_non_locked = false
  AND credentials_non_expired = false;
