-- The Sakila data comes with some passwords set, encoded as SHA1 or SHA2 - but we weren't able to figure out what
-- they are. SHA is also not very nice since the same password creates the same hash - the original data has the same
-- value for each. We replace this here with something else so we know the passwords, and since we are at it, we also
-- switch to Bcrypt.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

WITH constants (hash) as (
    values(crypt('password', gen_salt('bf', 12)))
)
UPDATE staff
SET password = constants.hash
FROM constants;
