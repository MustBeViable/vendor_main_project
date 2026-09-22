CREATE VIEW product_categories AS
SELECT products.name AS product, productcategories.name AS category
FROM products
         JOIN productcategories
              ON products.category_id = productcategories.id;

SELECT * FROM product_categories;

SELECT * FROM product_categories
WHERE category = 'Vaatteet';