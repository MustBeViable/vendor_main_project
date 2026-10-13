
-- 08_privileges.sql
-- Tietokantakäyttäjät ja käyttöoikeudet.
-- Käytetään aiemmissa tehtävissä määriteltyjä salasanoja.
-- Tarkoitettu paikalliseen kehitys- ja testikäyttöön.

USE store_app;


-- =========================================
-- 1. KÄYTTÄJIEN LUOMINEN
-- =========================================

-- Tietokannan ylläpitäjä.
CREATE USER IF NOT EXISTS 'db_admin'@'localhost'
IDENTIFIED BY 'secret_password';

-- Spring Boot -sovelluksen käyttäjä.
CREATE USER IF NOT EXISTS 'vendor_app'@'localhost'
IDENTIFIED BY 'very_very_secret_password';

-- Työntekijä.
CREATE USER IF NOT EXISTS 'employee'@'localhost'
IDENTIFIED BY 'another_secret_password';

-- Raportointikäyttäjä.
CREATE USER IF NOT EXISTS 'report_viewer'@'localhost'
IDENTIFIED BY 'VAIHDA_REPORT_VIEWER_SALASANA';


-- Päivitetään salasanat myös jo olemassa oleville käyttäjille.

ALTER USER 'db_admin'@'localhost'
IDENTIFIED BY 'secret_password';

ALTER USER 'vendor_app'@'localhost'
IDENTIFIED BY 'VAIHDA_VENDOR_APP_SALASANA';

ALTER USER 'employee'@'localhost'
IDENTIFIED BY 'another_secret_password';

ALTER USER 'report_viewer'@'localhost'
IDENTIFIED BY 'VAIHDA_REPORT_VIEWER_SALASANA';


-- =========================================
-- 2. AIEMPIEN OIKEUKSIEN POISTAMINEN
-- =========================================

REVOKE ALL PRIVILEGES, GRANT OPTION
    FROM 'db_admin'@'localhost';

REVOKE ALL PRIVILEGES, GRANT OPTION
    FROM 'vendor_app'@'localhost';

REVOKE ALL PRIVILEGES, GRANT OPTION
    FROM 'employee'@'localhost';

REVOKE ALL PRIVILEGES, GRANT OPTION
    FROM 'report_viewer'@'localhost';


-- =========================================
-- 3. DB_ADMIN
-- =========================================

-- Tietokannan rakenteen ja ominaisuuksien hallinta.

GRANT ALL PRIVILEGES
ON store_app.*
TO 'db_admin'@'localhost';


-- =========================================
-- 4. VENDOR_APP
-- =========================================

-- Asiakkaiden ja osoitteiden hallinta.

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.customers
          TO 'vendor_app'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.customeraddresses
          TO 'vendor_app'@'localhost';


-- Tuotteiden ja kategorioiden hallinta.

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.products
          TO 'vendor_app'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.productcategories
          TO 'vendor_app'@'localhost';


-- Tilausten ja tilausrivien hallinta.

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.orders
          TO 'vendor_app'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.orderitems
          TO 'vendor_app'@'localhost';


-- Toimittajien osoitteiden hallinta.

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.supplieraddresses
          TO 'vendor_app'@'localhost';


-- Toimittajien ja yhteystietojen lukeminen.

GRANT SELECT
      ON store_app.suppliers
          TO 'vendor_app'@'localhost';

GRANT SELECT
      ON store_app.contacts
          TO 'vendor_app'@'localhost';


-- Raportointinäkymien lukeminen.

GRANT SELECT
      ON store_app.customer_orders
          TO 'vendor_app'@'localhost';

GRANT SELECT
      ON store_app.product_categories
          TO 'vendor_app'@'localhost';

GRANT SELECT
      ON store_app.order_totals
          TO 'vendor_app'@'localhost';

GRANT SELECT
      ON store_app.daily_order_count
          TO 'vendor_app'@'localhost';


-- Varastokorjausten suorittaminen.

GRANT EXECUTE
ON PROCEDURE store_app.stock_increase
TO 'vendor_app'@'localhost';

GRANT EXECUTE
ON PROCEDURE store_app.stock_decrease
TO 'vendor_app'@'localhost';


-- =========================================
-- 5. EMPLOYEE
-- =========================================

-- Työntekijä saa tarkastella tilauksia ja tuotteita.

GRANT SELECT
      ON store_app.customer_orders
          TO 'employee'@'localhost';

GRANT SELECT
      ON store_app.product_categories
          TO 'employee'@'localhost';

GRANT SELECT
      ON store_app.order_totals
          TO 'employee'@'localhost';


-- =========================================
-- 6. REPORT_VIEWER
-- =========================================

-- Raportointikäyttäjällä on vain lukuoikeudet.

GRANT SELECT
      ON store_app.product_categories
          TO 'report_viewer'@'localhost';

GRANT SELECT
      ON store_app.order_totals
          TO 'report_viewer'@'localhost';

GRANT SELECT
      ON store_app.daily_order_count
          TO 'report_viewer'@'localhost';


-- =========================================
-- 7. OIKEUKSIEN TARKISTUS
-- =========================================

SHOW GRANTS FOR 'db_admin'@'localhost';
SHOW GRANTS FOR 'vendor_app'@'localhost';
SHOW GRANTS FOR 'employee'@'localhost';
SHOW GRANTS FOR 'report_viewer'@'localhost';
