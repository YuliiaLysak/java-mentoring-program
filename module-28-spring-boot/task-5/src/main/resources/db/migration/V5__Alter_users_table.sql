ALTER TABLE users
    ADD account_non_locked BOOLEAN NOT NULL DEFAULT true;
ALTER TABLE users
    ADD failed_attempt     int4    NOT NULL DEFAULT 0;
ALTER TABLE users
    ADD lock_time          timestamp        DEFAULT NULL;
