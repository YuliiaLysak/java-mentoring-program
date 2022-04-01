-- Test queries:
-- a. Find user by name (exact match)
EXPLAIN ANALYZE
SELECT name, surname, date_of_birth, phone_number, primary_skill
FROM students
WHERE name = 'Julia';

-- b. Find user by surname (partial match)
EXPLAIN ANALYZE
SELECT name, surname, date_of_birth, phone_number, primary_skill
FROM students
WHERE surname LIKE 'Dil%';

-- c. Find user by phone number (partial match)
EXPLAIN ANALYZE
SELECT name, surname, date_of_birth, phone_number, primary_skill
FROM students
WHERE students.phone_number LIKE '2-67%';


-- d. Find user with marks by user surname (partial match)

-- Advices:
-- create index on join column (surname)
-- LIKE "greg%" will use index but LIKE "%greg" will not
EXPLAIN ANALYZE
SELECT name, surname, mark, subject_name
FROM students st
         INNER JOIN exam_results er ON st.id = er.student_id
         INNER JOIN subjects su ON su.id = er.subject_id
WHERE surname LIKE 'Greg%';


-- 5. Add trigger that will update column updated_datetime
-- to current date in case of updating any of student.
CREATE OR REPLACE FUNCTION upd_datetime()
RETURNS TRIGGER AS $$
   BEGIN
       NEW.updated_datetime = now();
       RETURN NEW;
   END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER students_update_datetime BEFORE UPDATE
    ON students FOR EACH ROW EXECUTE PROCEDURE upd_datetime();


SELECT * FROM students WHERE id = 1;

UPDATE students
SET primary_skill = 'Health'
WHERE id = 1;

-- 7. Create snapshot that will contain next data:
-- student name, student surname, subject name, mark
-- (snapshot means that in case of changing some data in source table –
-- your snapshot should not change).

SELECT st.name,
       st.surname,
       su.subject_name,
       er.mark
INTO TABLE students_marks_snapshot
FROM students st
         INNER JOIN exam_results er ON st.id = er.student_id
         INNER JOIN subjects su ON su.id = er.subject_id;

CREATE MATERIALIZED VIEW mv_students_marks AS
SELECT st.name,
       st.surname,
       su.subject_name,
       er.mark
FROM students st
         INNER JOIN exam_results er ON st.id = er.student_id
         INNER JOIN subjects su ON su.id = er.subject_id;
