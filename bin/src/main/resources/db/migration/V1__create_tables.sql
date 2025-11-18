-- Categories table
CREATE TABLE categories(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    display_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Menu items table
CREATE TABLE menu_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK(price >= 0),
    category_id INT NOT NULL,
    preparation_time INT,
    available BOOLEAN DEFAULT TRUE,
    ingredients JSON,
    tags JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Restaurant tables table
CREATE TABLE restaurant_tables(
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(10) NOT NULL UNIQUE,
    capacity INT NOT NULL CHECK (capacity > 0),
    status ENUM('AVAILABLE','OCCUPIED','RESERVED') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    table_id INT NOT NULL,
    customer_name VARCHAR(100),
    status ENUM('CREATED','CONFIRMED','PREPARING','READY','DELIVERED','CANCELLED','PAID') DEFAULT 'CREATED',
    total_amount DECIMAL(10,2) DEFAULT 0 CHECK(total_amount >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES restaurant_tables(id)
);

-- Order items table
CREATE TABLE order_items(
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT NOT NULL CHECK(quantity > 0),
    notes TEXT,
    status ENUM('PENDING','COMPLETED','FAILED','REFUNDED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);

-- Payments table
CREATE TABLE payments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK(amount > 0),
    method ENUM('CREDIT_CARD','EXPRESS','DEBIT_CARD','PIX','PAYPAL','CASH','BANK_TRANSFER') NOT NULL,
    status ENUM('PENDING','COMPLETED','FAILED','REFUNDED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- =================================================================================
-- ESSENTIAL INDEXES
-- =================================================================================

-- Menu items indexes
CREATE INDEX idx_menu_items_categories ON menu_items(category_id);
CREATE INDEX idx_menu_items_available ON menu_items(available);
CREATE INDEX idx_menu_items_price ON menu_items(price);

-- Order indexes
CREATE INDEX idx_orders_table ON orders(table_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Order items indexes
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_status ON order_items(status);

-- Payments indexes
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);

-- =================================================================================
-- ESSENTIAL VIEWS (CORRIGIDAS)
-- =================================================================================

-- Active orders view
CREATE VIEW active_orders AS
SELECT
    o.id,
    o.table_id,
    t.table_number,
    o.customer_name,
    o.status,
    o.total_amount,
    o.created_at,
    COUNT(oi.id) as items_count
FROM orders o
JOIN restaurant_tables t ON o.table_id = t.id
LEFT JOIN order_items oi ON o.id = oi.order_id
WHERE o.status NOT IN ('CANCELLED','PAID')
GROUP BY o.id, t.table_number;

-- Daily sales view
CREATE VIEW daily_sales AS
SELECT
    DATE(p.created_at) as sale_date,
    COUNT(DISTINCT p.order_id) as orders_count,
    SUM(p.amount) as total_revenue,
    AVG(p.amount) as average_order_value
FROM payments p
WHERE p.status = 'COMPLETED'
GROUP BY DATE(p.created_at);

-- Menu items sales view
CREATE VIEW menu_items_sales AS
SELECT
    mi.id,
    mi.name,
    c.name as category_name,
    SUM(oi.quantity) as total_sold
FROM menu_items mi
JOIN categories c ON mi.category_id = c.id
JOIN order_items oi ON mi.id = oi.menu_item_id
JOIN orders o ON oi.order_id = o.id
WHERE o.status = 'PAID'
GROUP BY mi.id, mi.name, c.name;

-- Mensagem de sucesso
SELECT 'Database schema created successfully' as message;