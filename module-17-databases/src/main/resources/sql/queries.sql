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
