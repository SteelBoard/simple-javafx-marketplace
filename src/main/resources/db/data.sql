TRUNCATE TABLE order_items, orders, cart_items, carts, product_images, products, users RESTART IDENTITY CASCADE;

INSERT INTO users (name, password, role) VALUES
('login1', 'password', 'USER'),
('login2', 'password', 'USER'),
('login3', 'password', 'USER'),
('login4', 'password', 'USER'),
('login5', 'password', 'USER'),
('admin', 'admin123', 'ADMIN');

INSERT INTO products (name, description, price, seller_id, rating, sales, quantity, active) VALUES
('Мilk', 'tasty', 10.99, 1, 5.0, 0, 0, true),
('Мilk', 'tasty', 10.99, 1, 1.2, 0, 0, true),

('Мilk', 'tasty', 10.99, 2, 4.0, 0, 0, true),
('Мilk', 'tasty', 10.99, 2, 2.5, 0, 0, true),

('Мilk', 'tasty', 10.99, 3, 3.0, 0, 0, true),
('Мilk', 'tasty', 10.99, 3, 4.8, 0, 0, true),

('Мilk', 'tasty', 10.99, 4, 2.0, 0, 0, true),
('Мilk', 'tasty', 10.99, 4, 5.0, 0, 0, true),

('Мilk', 'tasty', 10.99, 5, 1.0, 0, 0, true),
('Мilk', 'tasty', 10.99, 5, 5.0, 0, 0, true);