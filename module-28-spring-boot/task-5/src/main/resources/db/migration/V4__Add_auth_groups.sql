CREATE TABLE auth_user_group
(
    id         BIGSERIAL    NOT NULL,
    email      VARCHAR(255) NOT NULL,
    auth_group VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email, auth_group)
);

ALTER TABLE auth_user_group
    ADD CONSTRAINT user_auth_user_group_fk
        FOREIGN KEY (email)
            REFERENCES users (email)
            ON DELETE CASCADE;

INSERT INTO auth_user_group (email, auth_group)
VALUES ('yuliia@com.ua', 'USER'),
       ('yuliia@com.ua', 'ADMIN'),
       ('jdoe@com.ua', 'USER');