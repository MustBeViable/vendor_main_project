
-- 06_events.sql
-- Luodaan ajastetut tapahtumat tilausten seurantaan.

USE store_app;


-- 1. DAILY_ORDER_COUNT
-- Tallentaa päivittäiset tilausmäärät.
-- Taulu luodaan vain, jos sitä ei ole olemassa.

CREATE TABLE IF NOT EXISTS daily_order_count (
                                                 date DATE PRIMARY KEY,
                                                 order_count INT NOT NULL DEFAULT 0
);


-- Poistetaan vanhat eventit uudelleenluontia varten.

DROP EVENT IF EXISTS check_old_orders;
DROP EVENT IF EXISTS update_daily_order_count;


DELIMITER //


-- 2. CHECK_OLD_ORDERS
-- Tarkistaa tilaukset tunnin välein.
-- Merkitsee yli 7 päivää NEW-tilassa olleet
-- tilaukset CHECK_REQUIRED-tilaan.

CREATE EVENT check_old_orders
ON SCHEDULE EVERY 1 HOUR
ON COMPLETION PRESERVE
ENABLE
DO
BEGIN
UPDATE orders
SET status = 'CHECK_REQUIRED'
WHERE status = 'NEW'
  AND order_date < NOW() - INTERVAL 7 DAY;
END//


-- 3. UPDATE_DAILY_ORDER_COUNT
-- Päivittää tunnin välein tämän ja edellisen
-- päivän tilausten lukumäärät.

CREATE EVENT update_daily_order_count
ON SCHEDULE EVERY 1 HOUR
ON COMPLETION PRESERVE
ENABLE
DO
BEGIN
INSERT INTO daily_order_count (date, order_count)
VALUES
    (
        CURDATE(),
        (
            SELECT COUNT(*)
            FROM orders
            WHERE order_date >= CURDATE()
              AND order_date < CURDATE() + INTERVAL 1 DAY
    )
    ),
    (
        CURDATE() - INTERVAL 1 DAY,
        (
            SELECT COUNT(*)
            FROM orders
            WHERE order_date >= CURDATE() - INTERVAL 1 DAY
              AND order_date < CURDATE()
        )
    )
ON DUPLICATE KEY UPDATE
                     order_count = VALUES(order_count);
END//


DELIMITER ;


-- TARKISTUS
-- Näytetään luodut eventit ja niiden tila.

SELECT
    EVENT_NAME,
    STATUS,
    INTERVAL_VALUE,
    INTERVAL_FIELD
FROM information_schema.EVENTS
WHERE EVENT_SCHEMA = DATABASE()
  AND EVENT_NAME IN (
                     'check_old_orders',
                     'update_daily_order_count'
    );


-- Tarkistetaan, onko ajastuspalvelu käytössä.

SHOW GLOBAL VARIABLES LIKE 'event_scheduler';


-- Tarkistetaan tilastotaulun olemassaolo.

SHOW TABLES LIKE 'daily_order_count';
