CREATE TABLE reviews
(
    review_id BIGSERIAL NOT NULL,
    user_id   int8    NOT NULL,
    recipe_id int8    NOT NULL,
    rating    int4    NOT NULL DEFAULT 0,
    comment   VARCHAR(4000),
    date      TIMESTAMP,
    PRIMARY KEY (review_id)
);

ALTER TABLE reviews
    ADD CONSTRAINT fk_review_user_id
        FOREIGN KEY (user_id)
            REFERENCES users
            ON DELETE CASCADE;

ALTER TABLE reviews
    ADD CONSTRAINT fk_review_recipe_id
        FOREIGN KEY (recipe_id)
            REFERENCES recipes
            ON DELETE CASCADE;