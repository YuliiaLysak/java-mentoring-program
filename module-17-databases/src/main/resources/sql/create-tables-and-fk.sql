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
            ON DELETE RESTRICT;

ALTER TABLE IF EXISTS exam_results
    ADD CONSTRAINT fk_student_id
        FOREIGN KEY (student_id)
            REFERENCES students
            ON DELETE RESTRICT;

ALTER TABLE IF EXISTS exam_results
    ADD CONSTRAINT fk_subject_id
        FOREIGN KEY (subject_id)
            REFERENCES subjects
            ON DELETE RESTRICT;

-- 6. Add validation on DB level that will check username
-- on special characters (reject student name with next
-- characters '@', '#', '$').

ALTER TABLE students
    ADD CONSTRAINT name_surname_check
        CHECK (
                name ~ '^[^@#$]+$'
                AND surname ~ '^[^@#$]+$'
            );

INSERT INTO students(name, surname, date_of_birth, phone_number, primary_skill, created_datetime, updated_datetime)
 VALUES ('John$', 'Dow#', now(), '2-677-377-8850', 'English', now(), now());

SELECT * FROM students WHERE id = 100003;

-- from console:
-- [2022-02-22 21:04:08] [23514] ERROR: new row for relation "students" violates check constraint "name_surname_check"
-- [2022-02-22 21:04:08] Detail: Failing row contains (100001, John$, Dow#, 2022-02-22 19:04:08.938862, 2-677-377-8850, English, 2022-02-22 19:04:08.938862, 2022-02-22 19:04:08.938862).
