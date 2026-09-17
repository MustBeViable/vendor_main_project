CREATE USER db_admin@localhost IDENTIFIED BY 'secret_password';

GRANT SELECT, INSERT, UPDATE, DELETE, ALTER ON TEST_DATABASE.* TO 'db_admin'@'localhost';

CREATE USER iso@localhost IDENTIFIED BY 'iso_password';

GRANT SELECT, INSERT, UPDATE ON TEST_DATABASE.* TO 'iso'@'localhost';

CREATE USER employee@localhost IDENTIFIED BY 'another_secret_password';

GRANT SELECT ON TEST_DATABASE.orderitems
    TO 'employee'@'localhost';

GRANT SELECT ON TEST_DATABASE.orders
    TO 'employee'@'localhost';

GRANT SELECT ON TEST_DATABASE.productcategories
    TO 'employee'@'localhost';

GRANT SELECT ON TEST_DATABASE.products
    TO 'employee'@'localhost';

GRANT SELECT ON TEST_DATABASE.supplieraddresses
    TO 'employee'@'localhost';

GRANT SELECT ON TEST_DATABASE.suppliers
    TO 'employee'@'localhost';

GRANT SELECT ON TEST_DATABASE.contacts
    TO 'employee'@'localhost';

GRANT SELECT (first_name, last_name)
          ON TEST_DATABASE.customers
TO 'employee'@'localhost';

GRANT INSERT, UPDATE, DELETE
              ON TEST_DATABASE.orders
                  TO 'employee'@'localhost';