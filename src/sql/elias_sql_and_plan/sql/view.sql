CREATE VIEW customer_orders AS
SELECT customers.first_name AS first_name,
       customers.last_name AS last_name,
       orderitems.quantity AS quantity,
       products.name AS product_name,
       orderitems.quantity * products.price AS total_price,
       orders.status AS order_status
FROM orders
         JOIN customers
              ON customers.id = orders.customer_id
         JOIN orderitems
              ON orders.id = orderitems.order_id
         JOIN products
              ON products.id = orderitems.product_id
where customers.id = 1
  AND orders.id = 1;