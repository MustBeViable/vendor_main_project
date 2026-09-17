START TRANSACTION;

INSERT INTO Orders (customer_id, delivery_date, status)
VALUES (1, '2026-08-26', 'NEW');

SET @order_id = LAST_INSERT_ID();

INSERT INTO OrderItems (order_id, product_id, quantity, unit_price)
VALUES (@order_id, 5, 2, 10.25);

COMMIT;