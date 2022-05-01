ALTER TABLE users
    ADD account_non_locked BOOLEAN NOT NULL DEFAULT true,
    ADD failed_attempt     int4    NOT NULL DEFAULT 0,
    ADD lock_time          timestamp        DEFAULT NULL;
