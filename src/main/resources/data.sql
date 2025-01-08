INSERT INTO products (name, description, price, is_available) VALUES ('Smart TV', 'See the world as you never did', 899.00, TRUE);
INSERT INTO products (name, description, price, is_available) VALUES ('Smartphone', 'The world in your hands', 1999.90, TRUE);
INSERT INTO products (name, description, price, is_available) VALUES ('Laptop', 'Incredibly fast, small and elegant', 4695.99, FALSE);
INSERT INTO products (name, description, price, is_available) VALUES ('Freezer', 'I''m freezing 🥶', 749.50, TRUE);

ALTER TABLE products ALTER COLUMN description VARCHAR(1000);