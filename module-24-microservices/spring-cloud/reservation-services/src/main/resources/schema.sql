CREATE TABLE reservations
(
    reservation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id        BIGINT NOT NULL,
    guest_id       BIGINT NOT NULL,
    res_date       VARCHAR(100)
);