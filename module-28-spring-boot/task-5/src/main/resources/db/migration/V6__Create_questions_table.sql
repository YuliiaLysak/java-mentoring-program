CREATE TABLE questions
(
    question_id BIGSERIAL NOT NULL,
    user_id     int8    NOT NULL,
    recipe_id   int8    NOT NULL,
    question    VARCHAR(4000),
    date        TIMESTAMP,
    PRIMARY KEY (question_id)
);

ALTER TABLE questions
    ADD CONSTRAINT fk_question_user_id
        FOREIGN KEY (user_id)
            REFERENCES users
            ON DELETE CASCADE;

ALTER TABLE questions
    ADD CONSTRAINT fk_question_recipe_id
        FOREIGN KEY (recipe_id)
            REFERENCES recipes
            ON DELETE CASCADE;