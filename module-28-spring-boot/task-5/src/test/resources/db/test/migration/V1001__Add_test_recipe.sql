INSERT INTO recipes(id, name, category, date, description, directions)
VALUES (1000, 'name', 'category', '2021-06-01 06:50', 'description', 'directions');

INSERT INTO products(id, name)
VALUES (1000, 'product1'),
       (1001, 'product2');

INSERT INTO ingredients(id, recipe_id, product_id, quantity, measurement_unit)
VALUES (1000, 1000, 1000, 1.0, 'inch'),
       (1001, 1000, 1001, 0.5, 'pcs');
