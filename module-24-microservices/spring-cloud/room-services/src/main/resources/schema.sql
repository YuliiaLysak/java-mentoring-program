CREATE TABLE rooms
(
    room_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(16) NOT NULL,
    room_number CHAR(2)     NOT NULL UNIQUE,
    bed_info    CHAR(2)     NOT NULL
);