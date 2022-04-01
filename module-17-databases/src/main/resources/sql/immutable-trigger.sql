CREATE TABLE student_address
(
    id            BIGSERIAL    NOT NULL,
    student_id    int8         NOT NULL,
    country       VARCHAR(255) NOT NULL,
    city          VARCHAR(255) NOT NULL,
    street_name   VARCHAR(255) NOT NULL,
    street_number int8         NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS student_address
    ADD CONSTRAINT fk_student_id
        FOREIGN KEY (student_id)
            REFERENCES students
            ON DELETE RESTRICT;

CREATE TABLE student_address_updated
(
    id            BIGSERIAL    NOT NULL,
    student_id    int8         NOT NULL,
    country       VARCHAR(255) NOT NULL,
    city          VARCHAR(255) NOT NULL,
    street_name   VARCHAR(255) NOT NULL,
    street_number int8         NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO student_address(student_id, country, city, street_name, street_number)
VALUES (1, 'Ukraine', 'Kyiv', 'Lesi Ukrainky', 12),
       (2, 'Ukraine', 'Lviv', 'Vynogradna', 10),
       (3, 'Ukraine', 'Odesa', 'Derybasivska', 9),
       (4, 'Ukraine', 'Kharkiv', 'Centralna', 32),
       (5, 'Ukraine', 'Dnipro', 'Naberezhna', 8);



CREATE OR REPLACE FUNCTION no_updates()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO student_address_updated (student_id, country, city, street_name, street_number)
    VALUES (NEW.student_id, NEW.country, NEW.city, NEW.street_name, NEW.street_number);

    RETURN NULL;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER no_update_trigger
    BEFORE UPDATE
    ON student_address
    FOR EACH ROW
EXECUTE PROCEDURE no_updates();

UPDATE student_address
SET street_name = 'new street'
WHERE city = 'Kyiv';
