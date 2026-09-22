DELIMITER //

CREATE TRIGGER update_stock_after_order
    AFTER INSERT ON orderitems
    FOR EACH ROW
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE id = NEW.product_id;
END //

DELIMITER ;

--trigger test
SELECT id, name, stock_quantity FROM products WHERE id = 1;

INSERT INTO orderitems (order_id, product_id, quantity, unit_price)
VALUES (3, 1, 3, 22.22);

SELECT id, name, stock_quantity FROM products WHERE id = 1;



--event
SET GLOBAL event_scheduler = ON;

CREATE TABLE daily_order_count (
                                   date DATE PRIMARY KEY,
                                   order_count INT
);

CREATE EVENT update_daily_order_count
    ON SCHEDULE EVERY 1 MINUTE
    DO
    INSERT INTO daily_order_count (date, order_count)
    VALUES (CURDATE(), (SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURDATE()))
    ON DUPLICATE KEY UPDATE order_count = VALUES(order_count);

--event test

INSERT INTO orders (customer_id, status)
VALUES (1, 'NEW');

SELECT * FROM daily_order_count;