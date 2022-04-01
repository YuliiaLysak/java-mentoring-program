INSERT INTO recipes(name, category, date, description, directions)
VALUES ('Warming Ginger Tea', 'beverage', '2021-06-01 06:50', 'Ginger tea is a warming drink for cool weather',
        'Place all ingredients in a mug and fill with warm water (not too hot so you keep the beneficial honey compounds in tact); Steep for 5-10 minutes; Drink and enjoy'),
       ('Fresh Mint Tea', 'beverage', '2021-06-10 10:50', 'Light, aromatic and refreshing beverage',
        'Boil water; Pour boiling hot water into a mug; Add fresh mint leaves; Mix and let the mint leaves seep for 3-5 minutes; Add honey and mix again');

INSERT INTO products(name)
VALUES ('boiled water'),
       ('honey'),
       ('fresh mint leaves'),
       ('ginger root, minced'),
       ('lemon, juiced'),
       ('manuka honey');

INSERT INTO ingredients(recipe_id, product_id, quantity, measurement_unit)
VALUES (1, 4, 1.0, 'inch'),
       (1, 5, 0.5, 'pcs'),
       (1, 6, 0.5, 'teaspoon'),
       (2, 1, 200, 'ml'),
       (2, 2, 1, 'teaspoon'),
       (2, 3, 10, 'gram');
