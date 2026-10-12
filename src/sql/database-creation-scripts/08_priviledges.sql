
-- 08_privileges.sql
-- Luodaan tietokantakäyttäjät ja rajataan käyttöoikeudet.
-- Vaihda esimerkkisalasanat vahvoihin salasanoihin ennen ajoa.
-- Älä tallenna oikeita salasanoja GitHubiin.

USE store_app;


-- =========================================
-- 1. KÄYTTÄJIEN LUOMINEN
-- =========================================

-- Tietokannan ylläpitäjä.
CREATE USER IF NOT EXISTS 'db_admin'@'localhost'
IDENTIFIED BY 'VAIHDA_DB_ADMIN_SALASANA';

-- Spring Boot -sovelluksen tietokantakäyttäjä.
CREATE USER IF NOT EXISTS 'vendor_app'@'localhost'
IDENTIFIED BY 'VAIHDA_VENDOR_APP_SALASANA';

-- Työntekijän rajattu tietokantakäyttäjä.
CREATE USER IF NOT EXISTS 'employee'@'localhost'
IDENTIFIED BY 'VAIHDA_EMPLOYEE_SALASANA';

-- Raportointikäyttäjä, jolla on vain lukuoikeuksia.
CREATE USER IF NOT EXISTS 'report_viewer'@'localhost'
IDENTIFIED BY 'VAIHDA_REPORT_VIEWER_SALASANA';


-- =========================================
-- 2. AIEMPIEN OIKEUKSIEN NOLLAAMINEN
-- =========================================

-- Poistetaan käyttäjien aiemmat suorat oikeudet.
-- Näin vanhat, liian laajat oikeudet eivät jää voimaan.
-- Huom: tämä ei poista erikseen myönnettyjä rooleja.

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

-- Saa hallita store_app-tietokannan rakennetta,
-- indeksejä, näkymiä, triggereitä, eventtejä
-- ja proseduureja.
-- Ei saa automaattisesti hallita muita tietokantoja.

GRANT ALL PRIVILEGES
ON store_app.*
TO 'db_admin'@'localhost';


-- =========================================
-- 4. VENDOR_APP
-- =========================================

-- Spring Boot -sovellus saa käsitellä asiakkaita.

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.customers
          TO 'vendor_app'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.customeraddresses
          TO 'vendor_app'@'localhost';


-- Saa käsitellä tuotteita ja kategorioita.

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.products
          TO 'vendor_app'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.productcategories
          TO 'vendor_app'@'localhost';


-- Saa käsitellä tilauksia ja tilausrivejä.

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.orders
          TO 'vendor_app'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.orderitems
          TO 'vendor_app'@'localhost';


-- Saa käsitellä toimittajien osoitteita.

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.supplieraddresses
          TO 'vendor_app'@'localhost';


-- Saa lukea toimittajien ja yhteystietojen tiedot.

GRANT SELECT
      ON store_app.suppliers
          TO 'vendor_app'@'localhost';

GRANT SELECT
      ON store_app.contacts
          TO 'vendor_app'@'localhost';


-- Saa lukea ylläpitäjän näkymiä.

GRANT SELECT
      ON store_app.customer_orders
          TO 'vendor_app'@'localhost';

GRANT SELECT
      ON store_app.product_categories
          TO 'vendor_app'@'localhost';

GRANT SELECT
      ON store_app.order_totals
          TO 'vendor_app'@'localhost';


-- Saa lukea päivittäisiä tilaustilastoja.

GRANT SELECT
      ON store_app.daily_order_count
          TO 'vendor_app'@'localhost';


-- Saa suorittaa varastokorjausproseduurit.

GRANT EXECUTE
ON PROCEDURE store_app.stock_increase
TO 'vendor_app'@'localhost';

GRANT EXECUTE
ON PROCEDURE store_app.stock_decrease
TO 'vendor_app'@'localhost';


-- =========================================
-- 5. EMPLOYEE
-- =========================================

-- Työntekijä saa tarkastella tilaustietoja
-- ja tuotteita näkymien kautta.
-- Ei suoria INSERT-, UPDATE- tai DELETE-oikeuksia.

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

-- Raportointikäyttäjä näkee tuotetiedot,
-- tilausten yhteenvetotiedot ja päivätilastot.
-- Ei pääsyä asiakkaiden nimiä sisältävään
-- customer_orders-näkymään.

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
-- 7. KÄYTTÖOIKEUKSIEN TARKISTUS
-- =========================================

-- Näyttää käyttäjille myönnetyt oikeudet.

SHOW GRANTS FOR 'db_admin'@'localhost';

SHOW GRANTS FOR 'vendor_app'@'localhost';

SHOW GRANTS FOR 'employee'@'localhost';

SHOW GRANTS FOR 'report_viewer'@'localhost';
