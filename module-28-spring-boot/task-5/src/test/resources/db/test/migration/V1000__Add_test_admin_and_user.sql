INSERT INTO users (email, password)
VALUES ('user@com.ua', '$2a$11$X.wpEgoasigk9RphYEVw3OZI8TaV7Sop7KcEgSMRguw6FcSvVQkNO'),
       ('admin@com.ua', '$2a$11$k9E5Vl/1WTv7mLEe/zUQuOG4.2I4znNQQc3jWqqiAyNj/tsPZMp0G');

INSERT INTO auth_user_group (email, auth_group)
VALUES ('user@com.ua', 'USER'),
       ('admin@com.ua', 'ADMIN'),
       ('admin@com.ua', 'USER');
