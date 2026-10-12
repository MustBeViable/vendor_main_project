
-- 05_procedures.sql
-- Luodaan proseduurit tuotteiden varastosaldojen hallintaan.

USE store_app;

-- Poistetaan aiemmat versiot, jotta skripti voidaan ajaa uudelleen.
DROP PROCEDURE IF EXISTS stock_increase;
DROP PROCEDURE IF EXISTS stock_decrease;

DELIMITER //


-- 1. STOCK_INCREASE
-- Lisää tuotteen varastosaldoa annetulla määrällä.
-- Tarkistaa määrän ja tuotteen olemassaolon.

CREATE PROCEDURE stock_increase(
    IN p_product_id INT,
    IN p_quantity INT
)
BEGIN
    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Invalid product ID';
END IF;

    IF p_quantity IS NULL OR p_quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Quantity must be positive';
END IF;

UPDATE products
SET stock_quantity = stock_quantity + p_quantity
WHERE id = p_product_id;

IF ROW_COUNT() <> 1 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Product not found';
END IF;
END//


-- 2. STOCK_DECREASE
-- Vähentää tuotteen varastosaldoa annetulla määrällä.
-- Estää varastosaldon menemisen negatiiviseksi.

CREATE PROCEDURE stock_decrease(
    IN p_product_id INT,
    IN p_quantity INT
)
BEGIN
    IF p_product_id IS NULL OR p_product_id <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Invalid product ID';
END IF;

    IF p_quantity IS NULL OR p_quantity <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Quantity must be positive';
END IF;

UPDATE products
SET stock_quantity = stock_quantity - p_quantity
WHERE id = p_product_id
  AND stock_quantity >= p_quantity;

IF ROW_COUNT() <> 1 THEN
        IF NOT EXISTS (
            SELECT 1
            FROM products
            WHERE id = p_product_id
        ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Product not found';
ELSE
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Insufficient stock';
END IF;
END IF;
END//


DELIMITER ;


-- TARKISTUS
-- Näyttää tietokantaan luodut proseduurit.

SELECT ROUTINE_NAME
FROM information_schema.ROUTINES
WHERE ROUTINE_SCHEMA = DATABASE()
  AND ROUTINE_TYPE = 'PROCEDURE'
  AND ROUTINE_NAME IN ('stock_increase', 'stock_decrease');
