CREATE TABLE users
(
    id       BIGSERIAL    NOT NULL,
    email    VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (email)
);

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

INSERT INTO users (email, password)
VALUES ('yuliia@com.ua', '$2a$11$dp4wMyuqYE3KSwIyQmWZFeUb7jCsHAdk7ZhFc0qGw6i5J124imQBi'),
       ('jdoe@com.ua', '$2a$11$3NO32OV1TGjap3xMpAEjmuiizitWuaSwUYz42aMtlxRliwJ8zm4Sm');

INSERT INTO auth_user_group (email, auth_group)
VALUES ('yuliia@com.ua', 'USER'),
       ('yuliia@com.ua', 'ADMIN'),
       ('jdoe@com.ua', 'USER');
