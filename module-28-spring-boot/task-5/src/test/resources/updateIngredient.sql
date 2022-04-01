INSERT INTO recipes(id, name, category, date, description, directions)
VALUES (25, 'Cacao', 'beverage', '2021-06-01 06:50', 'Hot drink for cool weather',
        'Place all ingredients in a mug and fill with warm milk');

INSERT INTO products(id, name)
VALUES (25, 'milk');

INSERT INTO ingredients(id, recipe_id, product_id, quantity, measurement_unit)
VALUES (25, 25, 25, 200.0, 'ml');