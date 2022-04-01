-- In the case of Postgres DB in a Docker container:
-- Docker (starting with version 1.8) supports copying files
-- directly to the container
-- (docker cp source_path <containerid>:container_path)
-- which is useful if there is no shared volume.
-- After that the Postgres COPY FROM can access the file in the container
-- at container_path.

COPY students (name, surname, date_of_birth, phone_number, primary_skill, created_datetime, updated_datetime)
    FROM '/students.csv'
    DELIMITER ','
    CSV HEADER;

COPY tutors (name, surname, created_datetime, updated_datetime)
    FROM '/tutors.csv'
    DELIMITER ','
    CSV HEADER;

COPY subjects (subject_name, tutor_id)
    FROM '/subjects.csv'
    DELIMITER ','
    CSV HEADER;

-- COPY exam_results (student_id, subject_id, mark)
--     FROM '/exam_results.csv'
--     DELIMITER ','
--     CSV HEADER;
INSERT INTO exam_results(student_id, subject_id, mark)
SELECT floor(random() * (1000 - 1 + 1) + 1)::int,
       floor(random() * (1000 - 1 + 1) + 1)::int,
       floor(random() * (100 - 0 + 1) + 0)::int
FROM generate_series(1, 1000000);
