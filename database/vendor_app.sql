
CREATE USER IF NOT EXISTS 'vendor_app'@'localhost'
IDENTIFIED BY 'very_very_secret_password';

ALTER USER 'vendor_app'@'localhost'
IDENTIFIED BY 'very_very_secret_password';

GRANT SELECT, INSERT, UPDATE, DELETE
      ON store_app.*
          TO 'vendor_app'@'localhost';

SHOW GRANTS FOR 'vendor_app'@'localhost';
