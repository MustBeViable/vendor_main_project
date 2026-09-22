ALTER TABLE orders ADD SYSTEM VERSIONING;

SELECT id, status FROM orders WHERE id = 1;

UPDATE orders SET status = 'SHIPPED' WHERE id = 1;

SELECT *, row_start AS transaction_time
FROM orders
         FOR SYSTEM_TIME ALL
WHERE id = 1;