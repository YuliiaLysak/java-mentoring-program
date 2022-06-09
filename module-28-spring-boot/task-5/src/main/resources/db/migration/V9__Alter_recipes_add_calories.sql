ALTER TABLE recipes
    ADD calories int4 NOT NULL DEFAULT 0;
ALTER TABLE recipes
    ADD protein int4 NOT NULL DEFAULT 0;
ALTER TABLE recipes
    ADD fat int4 NOT NULL DEFAULT 0;
ALTER TABLE recipes
    ADD carbohydrate int4 NOT NULL DEFAULT 0;