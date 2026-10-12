
-- databasen luontiscripti
-- Luo tietokannan, 9 taulua ja niiden väliset liitokset.
-- Ei populointia eikä erillisiä sovellusindeksejä.

CREATE DATABASE IF NOT EXISTS store_app
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE store_app;


-- =========================================
-- 1. CUSTOMERS
-- =========================================

CREATE TABLE IF NOT EXISTS customers (
                                         id INT NOT NULL AUTO_INCREMENT,
                                         first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(30) DEFAULT NULL,

    PRIMARY KEY (id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- 2. SUPPLIERS
-- =========================================

CREATE TABLE IF NOT EXISTS suppliers (
                                         id INT NOT NULL AUTO_INCREMENT,
                                         name VARCHAR(255) NOT NULL,
    contact_name VARCHAR(100) DEFAULT NULL,
    phone VARCHAR(30) DEFAULT NULL,
    email VARCHAR(255) DEFAULT NULL,

    PRIMARY KEY (id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- 3. PRODUCT CATEGORIES
-- =========================================

CREATE TABLE IF NOT EXISTS productcategories (
                                                 id INT NOT NULL AUTO_INCREMENT,
                                                 name VARCHAR(100) NOT NULL,
    description TEXT DEFAULT NULL,

    PRIMARY KEY (id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- 4. CONTACTS
-- =========================================

CREATE TABLE IF NOT EXISTS contacts (
                                        id INT NOT NULL AUTO_INCREMENT,
                                        email VARCHAR(255) NOT NULL,
    reference CHAR(32) NOT NULL,

    PRIMARY KEY (id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- 5. CUSTOMER ADDRESSES
-- =========================================

CREATE TABLE IF NOT EXISTS customeraddresses (
                                                 id INT NOT NULL AUTO_INCREMENT,
                                                 customer_id INT NOT NULL,
                                                 street_address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20) DEFAULT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) DEFAULT NULL,

    PRIMARY KEY (id),

    KEY fk_customeraddress_customer (customer_id),

    CONSTRAINT fk_customeraddress_customer
    FOREIGN KEY (customer_id)
    REFERENCES customers(id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- 6. SUPPLIER ADDRESSES
-- =========================================

CREATE TABLE IF NOT EXISTS supplieraddresses (
                                                 id INT NOT NULL AUTO_INCREMENT,
                                                 supplier_id INT NOT NULL,
                                                 street_address VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20) DEFAULT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) DEFAULT NULL,

    PRIMARY KEY (id),

    KEY fk_supplieraddress_supplier (supplier_id),

    CONSTRAINT fk_supplieraddress_supplier
    FOREIGN KEY (supplier_id)
    REFERENCES suppliers(id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- 7. PRODUCTS
-- =========================================

CREATE TABLE IF NOT EXISTS products (
                                        id INT NOT NULL AUTO_INCREMENT,
                                        name VARCHAR(255) NOT NULL,
    description TEXT DEFAULT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category_id INT DEFAULT NULL,
    supplier_id INT DEFAULT NULL,

    PRIMARY KEY (id),

    KEY fk_product_category (category_id),
    KEY fk_product_supplier (supplier_id),

    CONSTRAINT fk_product_category
    FOREIGN KEY (category_id)
    REFERENCES productcategories(id),

    CONSTRAINT fk_product_supplier
    FOREIGN KEY (supplier_id)
    REFERENCES suppliers(id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- 8. ORDERS
-- =========================================

CREATE TABLE IF NOT EXISTS orders (
                                      id INT NOT NULL AUTO_INCREMENT,
                                      customer_id INT NOT NULL,
                                      order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    delivery_date DATETIME DEFAULT NULL,
    shipping_address_id INT DEFAULT NULL,
    status VARCHAR(50) DEFAULT 'NEW',

    PRIMARY KEY (id),

    KEY fk_order_customer (customer_id),
    KEY fk_order_shipping_address (shipping_address_id),

    CONSTRAINT fk_order_customer
    FOREIGN KEY (customer_id)
    REFERENCES customers(id),

    CONSTRAINT fk_order_shipping_address
    FOREIGN KEY (shipping_address_id)
    REFERENCES customeraddresses(id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- 9. ORDER ITEMS
-- =========================================

CREATE TABLE IF NOT EXISTS orderitems (
                                          order_id INT NOT NULL,
                                          product_id INT NOT NULL,
                                          quantity INT NOT NULL,
                                          unit_price DECIMAL(10,2) NOT NULL,

    PRIMARY KEY (order_id, product_id),

    KEY fk_orderitem_product (product_id),

    CONSTRAINT fk_orderitem_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id),

    CONSTRAINT fk_orderitem_product
    FOREIGN KEY (product_id)
    REFERENCES products(id)
    ) ENGINE=InnoDB
    DEFAULT CHARSET=utf8mb4
    COLLATE=utf8mb4_unicode_ci;


-- =========================================
-- VERIFY TABLE CREATION
-- =========================================

SHOW TABLES;
