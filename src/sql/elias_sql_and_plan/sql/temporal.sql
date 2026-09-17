ALTER TABLE orders
    ADD SYSTEM VERSIONING;

ALTER TABLE products
    ADD SYSTEM VERSIONING;

SELECT *
FROM orders
WHERE now() between

UPDATE products
SET price = 200000.0
WHERE id = 1;

SELECT name, row_start, row_end
FROM products
         FOR SYSTEM_TIME ALL
WHERE id = 1;

SELECT MIN(price)
FROM products
         FOR SYSTEM_TIME ALL
WHERE id = 1
  AND row_start >= NOW() - INTERVAL 30 DAY;

UPDATE products
SET price = 2.0
WHERE id = 1;