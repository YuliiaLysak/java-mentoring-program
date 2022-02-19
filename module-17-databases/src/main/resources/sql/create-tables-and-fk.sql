-- 1. Design database for CDP program. Your DB should store information
-- about students (name, surname, date of birth, phone numbers, primary skill,
-- created_datetime, updated_datetime etc.), subjects (subject name, tutor, etc.)
-- and exam results (student, subject, mark).

CREATE TABLE students
(
    id               BIGSERIAL    NOT NULL,
    name             VARCHAR(255) NOT NULL,
    surname          VARCHAR(255) NOT NULL,
    date_of_birth    TIMESTAMP    NOT NULL,
    phone_number     VARCHAR(255) NOT NULL,
    primary_skill    VARCHAR(255) NOT NULL,
    created_datetime TIMESTAMP    NOT NULL,
    updated_datetime TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE tutors
(
    id               BIGSERIAL    NOT NULL,
    name             VARCHAR(255) NOT NULL,
    surname          VARCHAR(255) NOT NULL,
    created_datetime TIMESTAMP    NOT NULL,
    updated_datetime TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE subjects
(
    id           BIGSERIAL    NOT NULL,
    subject_name VARCHAR(255) NOT NULL,
    tutor_id     int8         NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE exam_results
(
    id         BIGSERIAL NOT NULL,
    student_id int8      NOT NULL,
    subject_id int8      NOT NULL,
    mark       int8      NOT NULL,
    PRIMARY KEY (id)
);


-- 2. Please add appropriate constraints (primary keys, foreign keys, indexes, etc.).

-- FOREIGN KEYS

ALTER TABLE IF EXISTS subjects
    ADD CONSTRAINT fk_tutor_id
        FOREIGN KEY (tutor_id)
            REFERENCES tutors
            ON
                DELETE
                RESTRICT;

ALTER TABLE IF EXISTS exam_results
    ADD CONSTRAINT fk_student_id
        FOREIGN KEY (student_id)
            REFERENCES students
            ON
                DELETE
                RESTRICT;

ALTER TABLE IF EXISTS exam_results
    ADD CONSTRAINT fk_subject_id
        FOREIGN KEY (subject_id)
            REFERENCES subjects
            ON
                DELETE
                RESTRICT;
