-- SmartCart Product Seed Data

-- Clear old data (prevents duplicates)
DELETE FROM products;

INSERT INTO products (name, description, price, stock_quantity, category, image_url) VALUES

-- Electronics
('Samsung Galaxy S24', 'Latest Samsung flagship with 6.2" Dynamic AMOLED display, 50MP camera, and Snapdragon 8 Gen 3.', 74999.00, 30, 'Electronics', 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=500&q=80'),
('Boat Airdopes 141', 'True wireless earbuds with 42 hours total playback, IPX4 water resistance, and instant connect.', 999.00, 100, 'Electronics', 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500&q=80'),
('Dell Inspiron 15', 'Intel Core i5 12th Gen laptop with 8GB RAM, 512GB SSD, and 15.6" FHD display.', 54990.00, 20, 'Electronics', 'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&q=80'),

-- Fashion
('Men Slim Fit Jeans', 'Classic slim-fit denim jeans in dark blue wash.', 1299.00, 80, 'Fashion', 'https://images.unsplash.com/photo-1542272604-787c3835535d?w=500&q=80'),
('Women Floral Kurta', 'Soft cotton floral print kurta.', 799.00, 120, 'Fashion', 'https://images.unsplash.com/photo-1610030169371-21a81b7e7b90?w=500&q=80'),
('Men Casual T-Shirt', 'Pack of 3 cotton T-shirts.', 599.00, 200, 'Fashion', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500&q=80'),

-- Footwear
('Nike Air Max 270', 'Comfortable running shoes.', 9995.00, 50, 'Footwear', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=500&q=80'),
('Bata Formal Shoes', 'Classic office shoes.', 1999.00, 60, 'Footwear', 'https://images.unsplash.com/photo-1614252369475-531eba835eb1?w=500&q=80'),
('Adidas Slides', 'Casual slides.', 699.00, 150, 'Footwear', 'https://images.unsplash.com/photo-1588361861040-ac9b1018f6d5?w=500&q=80'),

-- Groceries
('Amul Butter 500g', 'Pure butter.', 285.00, 300, 'Groceries', 'https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=500&q=80'),
('Tata Salt 1kg', 'Iodized salt.', 28.00, 500, 'Groceries', 'https://images.unsplash.com/photo-1518110925495-5fe2fda0442c?w=500&q=80'),
('Aashirvaad Atta 5kg', 'Whole wheat flour.', 270.00, 400, 'Groceries', 'https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?w=500&q=80'),

-- Accessories
('Fossil Gen 6 Watch', 'Smartwatch.', 12995.00, 25, 'Accessories', 'https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80'),
('Leather Wallet', 'RFID wallet.', 499.00, 180, 'Accessories', 'https://images.unsplash.com/photo-1627123424574-724758594e93?w=500&q=80'),
('Ray-Ban Sunglasses', 'UV protection sunglasses.', 3499.00, 40, 'Accessories', 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500&q=80');