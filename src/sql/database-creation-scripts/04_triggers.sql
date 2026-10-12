
-- 04_triggers.sql
-- Ylläpitää tuotteiden varastosaldoja automaattisesti,
-- kun tilausrivejä lisätään, muokataan tai poistetaan.

USE store_app;


-- Poistetaan vanhat päällekkäiset varastotriggerit.
-- Näin sama varastomuutos ei tapahdu kahdesti.

DROP TRIGGER IF EXISTS new_order;
DROP TRIGGER IF EXISTS cancelled_order;
DROP TRIGGER IF EXISTS update_stock_after_order;

-- Poistetaan aiemmat versiot uudelleenluontia varten.

DROP TRIGGER IF EXISTS orderitems_stock_insert;
DROP TRIGGER IF EXISTS orderitems_stock_update;
DROP TRIGGER IF EXISTS orderitems_stock_delete;


DELIMITER //


-- 1. INSERT
-- Vähentää tilatun määrän varastosta.
-- Estää negatiiviset määrät ja riittämättömän varastosaldon.

CREATE TRIGGER orderitems_stock_insert
    BEFORE INSERT ON orderitems
    FOR EACH ROW
BEGIN
    IF NEW.quantity IS NULL OR NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Quantity must be positive';
END IF;

UPDATE products
SET stock_quantity = stock_quantity - NEW.quantity
WHERE id = NEW.product_id
  AND stock_quantity >= NEW.quantity;

IF ROW_COUNT() <> 1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Insufficient stock';
END IF;
END//


-- 2. UPDATE
-- Muuttaa varastosaldoa tilausrivin määrän muutoksen mukaan.
-- Estää määrän kasvattamisen yli käytettävissä olevan saldon.

CREATE TRIGGER orderitems_stock_update
    BEFORE UPDATE ON orderitems
    FOR EACH ROW
BEGIN
    DECLARE quantity_difference INT DEFAULT 0;

    IF NEW.quantity IS NULL OR NEW.quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Quantity must be positive';
END IF;

IF NEW.order_id <> OLD.order_id
       OR NEW.product_id <> OLD.product_id THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Order or product ID cannot be changed';
END IF;

    SET quantity_difference = NEW.quantity - OLD.quantity;

    IF quantity_difference > 0 THEN

UPDATE products
SET stock_quantity = stock_quantity - quantity_difference
WHERE id = NEW.product_id
  AND stock_quantity >= quantity_difference;

IF ROW_COUNT() <> 1 THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Insufficient stock';
END IF;

    ELSEIF quantity_difference < 0 THEN

UPDATE products
SET stock_quantity = stock_quantity - quantity_difference
WHERE id = NEW.product_id;

END IF;
END//


-- 3. DELETE
-- Palauttaa poistetun tilausrivin tuotteet varastoon.

CREATE TRIGGER orderitems_stock_delete
    BEFORE DELETE ON orderitems
    FOR EACH ROW
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity + OLD.quantity
    WHERE id = OLD.product_id;
END//


DELIMITER ;


-- TARKISTUS
-- Näyttää kaikki orderitems-taulun triggerit.

SELECT
    TRIGGER_NAME,
    EVENT_MANIPULATION,
    ACTION_TIMING,
    EVENT_OBJECT_TABLE
FROM information_schema.TRIGGERS
WHERE TRIGGER_SCHEMA = DATABASE()
  AND EVENT_OBJECT_TABLE = 'orderitems'
ORDER BY EVENT_MANIPULATION;
