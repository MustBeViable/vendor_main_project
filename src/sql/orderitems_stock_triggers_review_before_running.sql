-- Optional MariaDB stock-trigger migration for the orderitems table.
-- IMPORTANT: inspect SHOW TRIGGERS LIKE 'orderitems'; before executing this script.
-- Run against a TEST database and take a backup first.
-- All old stock-changing orderitems triggers must be replaced, otherwise stock changes twice.
-- These are trigger names found in the repository scripts; inspect any additional deployed triggers.
--
-- SHOW TRIGGERS WHERE `Table` = 'orderitems';
-- SELECT id, stock_quantity FROM products WHERE id = 7;

DROP TRIGGER IF EXISTS new_order;
DROP TRIGGER IF EXISTS cancelled_order;
DROP TRIGGER IF EXISTS update_stock_after_order;
DROP TRIGGER IF EXISTS orderitems_stock_insert;
DROP TRIGGER IF EXISTS orderitems_stock_update;
DROP TRIGGER IF EXISTS orderitems_stock_delete;

DELIMITER //

CREATE TRIGGER orderitems_stock_insert
BEFORE INSERT ON orderitems
FOR EACH ROW
BEGIN
    IF NEW.quantity IS NULL OR NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Quantity must be positive';
    END IF;

    UPDATE products
       SET stock_quantity = stock_quantity - NEW.quantity
     WHERE id = NEW.product_id AND stock_quantity >= NEW.quantity;

    IF ROW_COUNT() <> 1 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock';
    END IF;
END//

CREATE TRIGGER orderitems_stock_update
BEFORE UPDATE ON orderitems
FOR EACH ROW
BEGIN
    DECLARE delta_qty INT DEFAULT 0;

    IF NEW.quantity IS NULL OR NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Quantity must be positive';
    END IF;

    IF NEW.order_id <> OLD.order_id OR NEW.product_id <> OLD.product_id THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Changing order/product ID is not supported';
    END IF;

    SET delta_qty = NEW.quantity - OLD.quantity;

    IF delta_qty > 0 THEN
        UPDATE products
           SET stock_quantity = stock_quantity - delta_qty
         WHERE id = NEW.product_id AND stock_quantity >= delta_qty;
        IF ROW_COUNT() <> 1 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock';
        END IF;
    ELSEIF delta_qty < 0 THEN
        UPDATE products
           SET stock_quantity = stock_quantity - delta_qty
         WHERE id = NEW.product_id;
    END IF;
END//

CREATE TRIGGER orderitems_stock_delete
BEFORE DELETE ON orderitems
FOR EACH ROW
BEGIN
    UPDATE products
       SET stock_quantity = stock_quantity + OLD.quantity
     WHERE id = OLD.product_id;
END//

DELIMITER ;

-- Verification idea (test DB only):
-- 1. Record product 7 stock.
-- 2. Create a NEW order and insert item product 7, quantity 2: stock decreases by 2.
-- 3. Update item quantity 2 -> 5: stock decreases by 3 more.
-- 4. Delete item: stock increases by 5, returning to initial balance.
-- 5. Attempt an excessive quantity: statement must fail and stock remain unchanged.
-- 6. Confirm that SHOW TRIGGERS lists exactly one stock-changing trigger per operation.
