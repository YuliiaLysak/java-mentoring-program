-- 4. Try different kind of indexes (B-tree, Hash, GIN, GIST) for your fields.
-- Analyze performance for each of the indexes (use ANALYZE and EXPLAIN).
-- Check the size of the index. Try to set index before inserting test data and after.
-- What was the time?

-- INDEXES

-- using extension pg_trgm for gin and gist indexes;
CREATE EXTENSION pg_trgm;
CREATE EXTENSION btree_gin;
CREATE EXTENSION btree_gist;

-- TABLE students
-- name
CREATE INDEX idx_btree_students_name
    ON students USING btree
        (name);

CREATE INDEX idx_hash_students_name
    ON students USING hash
        (name);

CREATE INDEX idx_gin_students_name
    ON students USING gin (to_tsvector('english', "name"));

CREATE INDEX idx_gist_students_name
    ON students USING gist (to_tsvector('english', "name"));

-- surname
CREATE INDEX idx_btree_students_surname
    ON students USING btree
        (surname);

CREATE INDEX idx_hash_students_surname
    ON students USING hash
        (surname);

-- CREATE INDEX idx_gin_students_surname
--     ON students USING gin (to_tsvector('english', "surname"));
CREATE INDEX idx_gin_trgm_students_surname
    ON students USING gin (surname gin_trgm_ops);

-- CREATE INDEX idx_gist_students_surname
--     ON students USING gist (to_tsvector('english', "surname"));
CREATE INDEX idx_gist_trgm_students_surname
    ON students USING gist (surname gist_trgm_ops);

-- phone_number
CREATE INDEX idx_btree_students_phone_number
    ON students USING btree
        (phone_number);

CREATE INDEX idx_hash_students_phone_number
    ON students USING hash
        (phone_number);

-- CREATE INDEX idx_gin_students_phone_number
--     ON students USING gin (to_tsvector('english', "phone_number"));
CREATE INDEX idx_gin_trgm_students_phone_number
    ON students USING gin (phone_number gin_trgm_ops);

-- CREATE INDEX idx_gist_students_phone_number
--     ON students USING gist (to_tsvector('english', "phone_number"));
CREATE INDEX idx_gist_trgm_students_phone_number
    ON students USING gist (phone_number gist_trgm_ops);


-- TABLE subjects
-- subject_name
CREATE INDEX idx_btree_subjects_subject_name
    ON subjects USING btree
        (subject_name);

CREATE INDEX idx_hash_subjects_subject_name
    ON subjects USING hash
        (subject_name);

CREATE INDEX idx_gin_subjects_subject_name
    ON subjects USING gin (to_tsvector('english', "subject_name"));

CREATE INDEX idx_gist_subjects_subject_name
    ON subjects USING gist (to_tsvector('english', "subject_name"));


-- TABLE exam_results
-- mark
CREATE INDEX idx_btree_exam_results_mark
    ON exam_results USING btree
        (mark);

CREATE INDEX idx_hash_exam_results_mark
    ON exam_results USING hash
        (mark);

CREATE INDEX idx_gin_exam_results_mark
    ON exam_results USING gin (mark);

CREATE INDEX idx_gist_exam_results_mark
    ON exam_results USING gist (mark);


-- This sql will give detailed info for table and index size
SELECT relname                                       as table_name,
       pg_size_pretty(pg_total_relation_size(relid)) As "Total Size",
       pg_size_pretty(pg_indexes_size(relid))        as "Index Size",
       pg_size_pretty(pg_relation_size(relid))       as "Actual Size"
FROM pg_catalog.pg_statio_user_tables
ORDER BY pg_total_relation_size(relid) DESC;
