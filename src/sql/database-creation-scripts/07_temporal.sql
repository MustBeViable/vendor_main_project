
-- 07_temporal.sql
-- Luodaan tilausten ja tuotteiden muutoshistoria.
-- Versionointi otetaan käyttöön vain, jos sitä ei vielä ole.

USE store_app;


-- 1. ORDERS
-- Tallentaa tilausten tietojen ja statusten historian.
-- Tarkistetaan ensin, onko versionointi jo käytössä.

SET @orders_sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'orders'
          AND TABLE_TYPE = 'SYSTEM VERSIONED'
    ),
    'SELECT ''orders: versionointi jo käytössä'' AS result',
    'ALTER TABLE orders ADD SYSTEM VERSIONING'
);

PREPARE stmt FROM @orders_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- 2. PRODUCTS
-- Tallentaa tuotteiden hintojen ja varastosaldojen historian.
-- Tarkistetaan ensin, onko versionointi jo käytössä.

SET @products_sql = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.TABLES
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'products'
          AND TABLE_TYPE = 'SYSTEM VERSIONED'
    ),
    'SELECT ''products: versionointi jo käytössä'' AS result',
    'ALTER TABLE products ADD SYSTEM VERSIONING'
);

PREPARE stmt FROM @products_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;


-- 3. TARKISTUS
-- Tarkistetaan, että molemmat taulut ovat versioituja.

SELECT
    TABLE_NAME,
    TABLE_TYPE,
    CASE
        WHEN TABLE_TYPE = 'SYSTEM VERSIONED'
            THEN 'OK'
        ELSE 'VERSIONOINTI PUUTTUU'
        END AS versioning_status
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = DATABASE()
  AND TABLE_NAME IN ('orders', 'products')
ORDER BY TABLE_NAME;


-- 4. TILAUSTEN HISTORIAN TARKISTUS
-- Näyttää nykyiset ja historialliset tilausversiot.

SELECT
    id,
    status,
    ROW_START AS version_start,
    ROW_END AS version_end
FROM orders FOR SYSTEM_TIME ALL
    LIMIT 10;


-- 5. TUOTTEIDEN HISTORIAN TARKISTUS
-- Näyttää nykyiset ja historialliset tuoteversiot.

SELECT
    id,
    name,
    price,
    stock_quantity,
    ROW_START AS version_start,
    ROW_END AS version_end
FROM products FOR SYSTEM_TIME ALL
    LIMIT 10;
