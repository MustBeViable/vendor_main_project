
delimiter //
CREATE PROCEDURE stock_update_decrease (IN pid INT, p_count INT)
BEGIN
UPDATE PRODUCTS
SET stock_quantity = stock_quantity - p_count
WHERE id = pid
  AND stock_quantity >= p_count;
END //
delimiter ;

delimiter //
CREATE PROCEDURE stock_update_increase (IN pid INT, p_count INT)
BEGIN
UPDATE PRODUCTS
SET stock_quantity = stock_quantity + p_count
WHERE id = pid;
END //
delimiter ;

select id, stock_quantity from products where id = 1;

CALL stock_update_increase (1, 11);

CALL stock_update_decrease (1, 11);

CREATE TRIGGER new_order
    AFTER INSERT ON orderitems
    FOR EACH ROW
    UPDATE PRODUCTS
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE id = NEW.product_id;

CREATE TRIGGER cancelled_order
    AFTER DELETE ON ORDERITEMS
    FOR EACH ROW
    UPDATE PRODUCTS
    SET stock_quantity = stock_quantity + OLD.quantity
    WHERE id = OLD.product_id;

SELECT id, stock_quantity
FROM products
WHERE id = 1;

INSERT INTO orders (customer_id)
VALUES (1);

select id, status from orders order by id desc limit 1;

select id, status from orders where id = 200008;

order id on 200004

INSERT INTO orderitems (order_id, product_id, quantity, unit_price)
VALUES (200004, 1, 1, 22.22);

DELETE FROM orderitems
WHERE order_id = 200004
  AND product_id = 1;

DELIMITER //
CREATE EVENT check_old_orders
ON SCHEDULE EVERY 1 MINUTE
DO
BEGIN
UPDATE orders
SET status = 'CHECK_REQUIRED'
WHERE status = 'NEW'
  AND order_date < NOW() - INTERVAL 1 MINUTE;
END;
//
DELIMITER ;

