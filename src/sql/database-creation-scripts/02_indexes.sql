-- 02_indexes.sql
-- Verkkokaupan ylläpitäjän hakujen nopeuttamiseen tarkoitetut indeksit.
-- Indeksi luodaan vain, jos samannimistä indeksiä ei ole vielä olemassa.

USE store_app;

-- 1. Nopeuttaa tilausten hakua tilan ja päivämäärän perusteella.
CREATE INDEX IF NOT EXISTS idx_orders_status_order_date
    ON orders (status, order_date);

-- 2. Nopeuttaa tuotteiden hakua nimen perusteella.
CREATE INDEX IF NOT EXISTS idx_products_name
    ON products (name);

-- 3. Nopeuttaa asiakkaan tilaushistorian hakemista päivämäärän mukaan.
CREATE INDEX IF NOT EXISTS idx_orders_customer_order_date
    ON orders (customer_id, order_date);

-- 4. Nopeuttaa vähissä olevien tuotteiden hakemista varastosaldon perusteella.
CREATE INDEX IF NOT EXISTS idx_products_stock_quantity
    ON products (stock_quantity);


-- Tarkistuskyselyt (ajetaan käsin, ei osana migraatiota):
-- SHOW INDEX FROM orders;
-- SHOW INDEX FROM products;

-- EXPLAIN-todistukset. :
-- EXPLAIN SELECT * FROM products WHERE name LIKE 'Sw%';
--   key: idx_products_name
-- EXPLAIN SELECT * FROM products WHERE stock_quantity <= 10;
--   key: idx_products_stock_quantity
-- EXPLAIN SELECT * FROM orders WHERE status = 'NEW' ORDER BY order_date DESC;
--   key: idx_orders_status_order_date
-- EXPLAIN SELECT * FROM orders WHERE customer_id = 1 ORDER BY order_date DESC;
--   key: idx_orders_customer_order_date