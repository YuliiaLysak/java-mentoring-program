-- 8. Create function that will return average mark for input user.

CREATE OR REPLACE FUNCTION average_mark_for_student(st_id int8)
    RETURNS float8 AS $av_mark_for_student$
DECLARE
    student_average_mark float8;
BEGIN
    SELECT avg(mark)
    INTO student_average_mark
    FROM exam_results er
    WHERE er.student_id = st_id;

    RETURN student_average_mark;
END;
$av_mark_for_student$ LANGUAGE plpgsql;

SELECT average_mark_for_student(1);


-- 9. Create function that will return average mark for input subject name.

CREATE OR REPLACE FUNCTION average_mark_for_subject_name(sub_name VARCHAR)
    RETURNS float8 AS $av_mark_for_subject$
DECLARE
    subject_average_mark float8;
BEGIN
    SELECT avg(mark)
    INTO subject_average_mark
    FROM exam_results er
    INNER JOIN subjects su on su.id = er.subject_id
    WHERE su.subject_name = sub_name;

    RETURN subject_average_mark;
END;
$av_mark_for_subject$ LANGUAGE plpgsql;

SELECT average_mark_for_subject_name('Spanish');


-- 10. Create function that will return student at "red zone"
-- (red zone means at least 2 marks <=3).

CREATE OR REPLACE FUNCTION red_zone_student()
    RETURNS TABLE(name VARCHAR, surname VARCHAR, date_of_birth timestamp, phone_number VARCHAR)
    AS $$
BEGIN
    RETURN QUERY
        SELECT st.name, st.surname, st.date_of_birth, st.phone_number
        FROM students st
            INNER JOIN exam_results er ON st.id = er.student_id
        GROUP BY st.name, st.surname, st.date_of_birth, st.phone_number
        HAVING count(er.mark <= 3) > 2;
END;
$$ LANGUAGE plpgsql;

SELECT red_zone_student();
