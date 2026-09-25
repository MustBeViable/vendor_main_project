CREATE USER db_admin@localhost IDENTIFIED BY 'secret_password';

GRANT SELECT, INSERT, UPDATE, DELETE, ALTER ON store_app.* TO 'db_admin'@'localhost';

CREATE USER iso@localhost IDENTIFIED BY 'iso_password';

GRANT SELECT, INSERT, UPDATE ON store_app.* TO 'iso'@'localhost';

CREATE USER employee@localhost IDENTIFIED BY 'another_secret_password';

GRANT SELECT ON store_app.orderitems
    TO 'employee'@'localhost';

GRANT SELECT ON store_app.orders
    TO 'employee'@'localhost';

GRANT SELECT ON store_app.productcategories
    TO 'employee'@'localhost';

GRANT SELECT ON store_app.products
    TO 'employee'@'localhost';

GRANT SELECT ON store_app.supplieraddresses
    TO 'employee'@'localhost';

GRANT SELECT ON store_app.suppliers
    TO 'employee'@'localhost';

GRANT SELECT ON store_app.contacts
    TO 'employee'@'localhost';

GRANT SELECT (first_name, last_name)
    ON store_app.customers
    TO 'employee'@'localhost';

GRANT INSERT, UPDATE, DELETE
    ON store_app.orders
    TO 'employee'@'localhost';