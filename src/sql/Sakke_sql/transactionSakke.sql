START TRANSACTION;

INSERT INTO products (name, price, stock_quantity, category_id)
VALUES ('Uusi tuote', 29.90, 10, 1);

COMMIT;

SELECT * FROM products
WHERE name = 'Uusi tuote';


START TRANSACTION;

SELECT stock_quantity
FROM products
WHERE id = 1
    FOR UPDATE;

UPDATE products
SET stock_quantity = stock_quantity - 4
WHERE id = 1;

COMMIT;

SELECT id, name, stock_quantity
FROM products
WHERE id = 1;