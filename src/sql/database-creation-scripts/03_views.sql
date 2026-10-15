
-- 03_views.sql
-- Luodaan verkkokaupan ylläpitäjän tarvitsemat näkymät.

USE store_app;


-- 1. CUSTOMER_ORDERS
-- Näyttää asiakkaiden tilaukset, tilausrivit ja niiden hinnat.
-- Rivisumma lasketaan tilaushetken yksikköhinnasta.

CREATE OR REPLACE VIEW customer_orders AS
SELECT
    CONCAT(o.id, '-', COALESCE(oi.product_id, 'none')) AS report_row_id,
    o.id AS order_id,
    o.customer_id,
    c.first_name,
    c.last_name,
    o.order_date,
    o.status AS order_status,
    oi.product_id,
    p.name AS product_name,
    oi.quantity,
    oi.unit_price,
    COALESCE(oi.quantity * oi.unit_price, 0.00) AS line_total
FROM orders o
         JOIN customers c
              ON o.customer_id = c.id
         LEFT JOIN orderitems oi
                   ON o.id = oi.order_id
         LEFT JOIN products p
                   ON oi.product_id = p.id;


-- 2. PRODUCT_CATEGORIES
-- Näyttää tuotteet, niiden kategoriat, hinnat ja varastosaldot.
-- Myös kategorioimattomat tuotteet näytetään.

CREATE OR REPLACE VIEW product_categories AS
SELECT
    p.id AS product_id,
    p.name AS product_name,
    p.category_id,
    pc.name AS category_name,
    p.price,
    p.stock_quantity
FROM products p
         LEFT JOIN productcategories pc
                   ON p.category_id = pc.id;


-- 3. ORDER_TOTALS
-- Laskee tilausten kokonaissummat ja kappalemäärät.
-- Näyttää myös tilaukset, joilla ei ole tilausrivejä.

CREATE OR REPLACE VIEW order_totals AS
SELECT
    o.id AS order_id,
    o.customer_id,
    o.order_date,
    o.status,
    COALESCE(SUM(oi.quantity), 0) AS total_quantity,
    COALESCE(SUM(oi.quantity * oi.unit_price), 0.00) AS total_amount
FROM orders o
         LEFT JOIN orderitems oi
                   ON o.id = oi.order_id
GROUP BY
    o.id,
    o.customer_id,
    o.order_date,
    o.status;


-- TARKISTUS
-- Näytetään tietokantaan luodut näkymät.

SELECT TABLE_NAME
FROM information_schema.VIEWS
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME;
