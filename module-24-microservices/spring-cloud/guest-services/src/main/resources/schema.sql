CREATE TABLE guests
(
    guest_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(64),
    last_name     VARCHAR(64),
    email_address VARCHAR(64),
    address       VARCHAR(64),
    country       VARCHAR(32),
    state         VARCHAR(12),
    phone_number  VARCHAR(24)
);