
-- 09_verify.sql
-- Tarkistetaan verkkokaupan tietokannan rakenne ja ominaisuudet.
-- Näytetään jokaiselle tarkistukselle OK tai PUUTTUU.
-- Skripti ei muuta tietokannan sisältöä.

USE store_app;


WITH

-- 1. Määritellään kaikki odotetut tietokantaominaisuudet.
-- Muoto: tyyppi, nimi, kohde, odotettu arvo.

expected AS (

    -- TAULUT (9 perustaulua + tilastotaulu)

    SELECT 'TABLE' AS type, 'customers' AS name,
           '' AS target, '' AS expected

    UNION ALL SELECT 'TABLE', 'customeraddresses', '', ''
    UNION ALL SELECT 'TABLE', 'suppliers', '', ''
    UNION ALL SELECT 'TABLE', 'supplieraddresses', '', ''
    UNION ALL SELECT 'TABLE', 'productcategories', '', ''
    UNION ALL SELECT 'TABLE', 'products', '', ''
    UNION ALL SELECT 'TABLE', 'orders', '', ''
    UNION ALL SELECT 'TABLE', 'orderitems', '', ''
    UNION ALL SELECT 'TABLE', 'contacts', '', ''
    UNION ALL SELECT 'TABLE', 'daily_order_count', '', ''


    -- PÄÄAVAIMET

    UNION ALL SELECT 'PRIMARY KEY', 'customers', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'customeraddresses', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'suppliers', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'supplieraddresses', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'productcategories', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'products', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'orders', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'orderitems', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'contacts', '', ''
    UNION ALL SELECT 'PRIMARY KEY', 'daily_order_count', '', ''


    -- VIITEAVAIMET
    -- Tarkistetaan viiteavaimen nimi ja kohdetietokantataulu.

    UNION ALL SELECT 'FOREIGN KEY',
                     'fk_customeraddress_customer',
                     'customeraddresses', 'customers'

    UNION ALL SELECT 'FOREIGN KEY',
                     'fk_supplieraddress_supplier',
                     'supplieraddresses', 'suppliers'

    UNION ALL SELECT 'FOREIGN KEY',
                     'fk_product_category',
                     'products', 'productcategories'

    UNION ALL SELECT 'FOREIGN KEY',
                     'fk_product_supplier',
                     'products', 'suppliers'

    UNION ALL SELECT 'FOREIGN KEY',
                     'fk_order_customer',
                     'orders', 'customers'

    UNION ALL SELECT 'FOREIGN KEY',
                     'fk_order_shipping_address',
                     'orders', 'customeraddresses'

    UNION ALL SELECT 'FOREIGN KEY',
                     'fk_orderitem_order',
                     'orderitems', 'orders'

    UNION ALL SELECT 'FOREIGN KEY',
                     'fk_orderitem_product',
                     'orderitems', 'products'


    -- INDEKSIT
    -- Tarkistetaan indeksin nimi ja sarakkeiden järjestys.

    UNION ALL SELECT 'INDEX',
                     'idx_orders_status_order_date',
                     'orders', 'status,order_date'

    UNION ALL SELECT 'INDEX',
                     'idx_products_name',
                     'products', 'name'

    UNION ALL SELECT 'INDEX',
                     'idx_orders_customer_order_date',
                     'orders', 'customer_id,order_date'

    UNION ALL SELECT 'INDEX',
                     'idx_products_stock_quantity',
                     'products', 'stock_quantity'


    -- NÄKYMÄT

    UNION ALL SELECT 'VIEW', 'customer_orders', '', ''
    UNION ALL SELECT 'VIEW', 'product_categories', '', ''
    UNION ALL SELECT 'VIEW', 'order_totals', '', ''


    -- TRIGGERIT
    -- Kaikkien pitää olla BEFORE-triggereitä.

    UNION ALL SELECT 'TRIGGER',
                     'orderitems_stock_insert',
                     'orderitems', 'INSERT'

    UNION ALL SELECT 'TRIGGER',
                     'orderitems_stock_update',
                     'orderitems', 'UPDATE'

    UNION ALL SELECT 'TRIGGER',
                     'orderitems_stock_delete',
                     'orderitems', 'DELETE'


    -- Tarkistetaan, ettei orderitems-taulussa
    -- ole ylimääräisiä triggereitä.

    UNION ALL SELECT 'TRIGGER COUNT',
                     'orderitems', '', '3'


    -- PROSEDUURIT

    UNION ALL SELECT 'PROCEDURE', 'stock_increase', '', ''
    UNION ALL SELECT 'PROCEDURE', 'stock_decrease', '', ''


    -- EVENTIT
    -- Molempien tulee olla käytössä kerran tunnissa.

    UNION ALL SELECT 'EVENT', 'check_old_orders', '', ''
    UNION ALL SELECT 'EVENT', 'update_daily_order_count', '', ''


    -- TEMPORAALISUUS

    UNION ALL SELECT 'TEMPORAL', 'orders', '', ''
    UNION ALL SELECT 'TEMPORAL', 'products', '', ''


    -- KESKEISET KÄYTTÖOIKEUDET
    -- Tarkistetaan kullekin käyttäjälle yksi vaadittu oikeus.

    UNION ALL SELECT 'ACCESS',
                     'db_admin', 'store_app', 'ALTER'

    UNION ALL SELECT 'ACCESS',
                     'vendor_app', 'products', 'SELECT'

    UNION ALL SELECT 'ACCESS',
                     'employee', 'customer_orders', 'SELECT'

    UNION ALL SELECT 'ACCESS',
                     'report_viewer', 'order_totals', 'SELECT'


    -- EVENT SCHEDULER

    UNION ALL SELECT 'SCHEDULER',
                     'event_scheduler', '', 'ON'
),


-- 2. Kerätään nykyisten indeksien sarakkeet.

existing_indexes AS (
    SELECT
        TABLE_NAME,
        INDEX_NAME,
        GROUP_CONCAT(
                COLUMN_NAME
                    ORDER BY SEQ_IN_INDEX
            SEPARATOR ','
        ) AS index_columns
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
    GROUP BY TABLE_NAME, INDEX_NAME
),


-- 3. Verrataan odotettuja ominaisuuksia tietokantaan.

checks AS (
    SELECT
        e.type,
        e.name,

        CASE

            -- TAULUT
            WHEN e.type = 'TABLE' THEN EXISTS (
                SELECT 1
                FROM information_schema.TABLES t
                WHERE t.TABLE_SCHEMA = DATABASE()
                  AND t.TABLE_NAME = e.name
                  AND t.TABLE_TYPE IN (
                                       'BASE TABLE',
                                       'SYSTEM VERSIONED'
                    )
            )


            -- PÄÄAVAIMET
            WHEN e.type = 'PRIMARY KEY' THEN EXISTS (
                SELECT 1
                FROM information_schema.TABLE_CONSTRAINTS c
                WHERE c.CONSTRAINT_SCHEMA = DATABASE()
                  AND c.TABLE_NAME = e.name
                  AND c.CONSTRAINT_TYPE = 'PRIMARY KEY'
            )


            -- VIITEAVAIMET
            WHEN e.type = 'FOREIGN KEY' THEN EXISTS (
                SELECT 1
                FROM information_schema.KEY_COLUMN_USAGE k
                WHERE k.CONSTRAINT_SCHEMA = DATABASE()
                  AND k.TABLE_NAME = e.target
                  AND k.CONSTRAINT_NAME = e.name
                  AND k.REFERENCED_TABLE_NAME = e.expected
            )


            -- INDEKSIT
            WHEN e.type = 'INDEX' THEN EXISTS (
                SELECT 1
                FROM existing_indexes i
                WHERE i.TABLE_NAME = e.target
                  AND i.INDEX_NAME = e.name
                  AND i.index_columns = e.expected
            )


            -- NÄKYMÄT
            WHEN e.type = 'VIEW' THEN EXISTS (
                SELECT 1
                FROM information_schema.VIEWS v
                WHERE v.TABLE_SCHEMA = DATABASE()
                  AND v.TABLE_NAME = e.name
            )


            -- TRIGGERIT
            WHEN e.type = 'TRIGGER' THEN EXISTS (
                SELECT 1
                FROM information_schema.TRIGGERS t
                WHERE t.TRIGGER_SCHEMA = DATABASE()
                  AND t.TRIGGER_NAME = e.name
                  AND t.EVENT_OBJECT_TABLE = e.target
                  AND t.EVENT_MANIPULATION = e.expected
                  AND t.ACTION_TIMING = 'BEFORE'
            )


            -- TRIGGERIEN KOKONAISMÄÄRÄ
            WHEN e.type = 'TRIGGER COUNT' THEN (
                                                   SELECT COUNT(*)
                                                   FROM information_schema.TRIGGERS t
                                                   WHERE t.TRIGGER_SCHEMA = DATABASE()
                                                     AND t.EVENT_OBJECT_TABLE = e.name
                                               ) = 3


            -- PROSEDUURIT
            WHEN e.type = 'PROCEDURE' THEN EXISTS (
                SELECT 1
                FROM information_schema.ROUTINES r
                WHERE r.ROUTINE_SCHEMA = DATABASE()
                  AND r.ROUTINE_NAME = e.name
                  AND r.ROUTINE_TYPE = 'PROCEDURE'
            )


            -- EVENTIT
            WHEN e.type = 'EVENT' THEN EXISTS (
                SELECT 1
                FROM information_schema.EVENTS ev
                WHERE ev.EVENT_SCHEMA = DATABASE()
                  AND ev.EVENT_NAME = e.name
                  AND ev.STATUS = 'ENABLED'
                  AND ev.EVENT_TYPE = 'RECURRING'
                  AND ev.INTERVAL_VALUE = '1'
                  AND ev.INTERVAL_FIELD = 'HOUR'
            )


            -- TEMPORAALISUUS
            WHEN e.type = 'TEMPORAL' THEN EXISTS (
                SELECT 1
                FROM information_schema.TABLES t
                WHERE t.TABLE_SCHEMA = DATABASE()
                  AND t.TABLE_NAME = e.name
                  AND t.TABLE_TYPE = 'SYSTEM VERSIONED'
            )


            -- DB_ADMIN-OIKEUS
            WHEN e.type = 'ACCESS'
                AND e.name = 'db_admin' THEN EXISTS (
                SELECT 1
                FROM information_schema.SCHEMA_PRIVILEGES p
                WHERE p.TABLE_SCHEMA = DATABASE()
                  AND p.GRANTEE = CONCAT(
                    CHAR(39), e.name,
                    CHAR(39), '@',
                    CHAR(39), 'localhost', CHAR(39)
                  )
                  AND p.PRIVILEGE_TYPE = e.expected
            )


            -- MUIDEN KÄYTTÄJIEN OIKEUDET
            WHEN e.type = 'ACCESS' THEN EXISTS (
                SELECT 1
                FROM information_schema.TABLE_PRIVILEGES p
                WHERE p.TABLE_SCHEMA = DATABASE()
                  AND p.TABLE_NAME = e.target
                  AND p.GRANTEE = CONCAT(
                    CHAR(39), e.name,
                    CHAR(39), '@',
                    CHAR(39), 'localhost', CHAR(39)
                  )
                  AND p.PRIVILEGE_TYPE = e.expected
            )


            -- EVENT SCHEDULER
            WHEN e.type = 'SCHEDULER' THEN (
                @@GLOBAL.event_scheduler = 'ON'
                )

            ELSE 0

            END AS is_ok

    FROM expected e
)


-- 4. Näytetään kaikkien ominaisuuksien tarkistustulokset.

SELECT
    type AS ominaisuus,
    name AS nimi,
    CASE
        WHEN is_ok = 1 THEN 'OK'
        ELSE 'PUUTTUU / VIRHEELLINEN'
        END AS tila
FROM checks

UNION ALL

-- 5. Yhteenveto kaikkien tarkistusten tuloksista.

SELECT
    'YHTEENVETO',
    CONCAT(
            SUM(is_ok), ' / ', COUNT(*),
            ' tarkistusta OK'
    ),
    CASE
        WHEN MIN(is_ok) = 1 THEN 'KAIKKI OK'
        ELSE 'TARKISTA PUUTTEET'
        END
FROM checks

ORDER BY ominaisuus, nimi;
