-- 1. Inserir Categories
INSERT INTO category (name, description, display_order) VALUES
('Beverages', 'Refrigerantes, sucos e bebidas', 1),
('Appetizers', 'Entradas e petiscos', 2),
('Main Courses', 'Pratos principais', 3),
('Desserts', 'Sobremesas e doces', 4),
('Specials', 'Pratos especiais do dia', 5);

-- 2. Inserir Menu Items
INSERT INTO menu_item (name, description, price, category_id, preparation_time, available) VALUES
-- Beverages
('Coca-Cola', 'Refrigerante Coca-Cola 350ml', 8.50, 1, 2, TRUE),
('Orange Juice', 'Suco de laranja natural 300ml', 12.00, 1, 3, TRUE),
('Coffee', 'Café expresso', 5.00, 1, 2, TRUE),

-- Appetizers
('Garlic Bread', 'Pão de alho com queijo', 15.00, 2, 5, TRUE),
('Chicken Wings', 'Asinhas de frango com molho barbecue', 25.00, 2, 10, TRUE),

-- Main Courses
('Grilled Salmon', 'Salmão grelhado com legumes', 45.00, 3, 15, TRUE),
('Beef Steak', 'Bife ancho 300g com fritas', 55.00, 3, 12, TRUE),
('Vegetarian Pasta', 'Massa integral com legumes', 32.00, 3, 10, TRUE),

-- Desserts
('Chocolate Cake', 'Bolo de chocolate com calda', 18.00, 4, 3, TRUE),
('Ice Cream', 'Sorvete de baunilha com calda', 12.00, 4, 2, TRUE);

-- 3. Inserir Restaurant Tables
INSERT INTO restaurant_table (table_number, capacity, status) VALUES
('T-01', 4, 'AVAILABLE'),
('T-02', 4, 'AVAILABLE'),
('T-03', 6, 'AVAILABLE'),
('T-04', 2, 'AVAILABLE'),
('T-05', 8, 'AVAILABLE'),
('T-06', 4, 'AVAILABLE');

-- 4. Inserir Orders (os order_items serão inseridos depois)
INSERT INTO orders (table_id, customer_name, status, total_amount) VALUES
(1, 'João Silva', 'CONFIRMED', 0),
(2, 'Maria Santos', 'PREPARING', 0),
(3, 'Carlos Oliveira', 'CREATED', 0);

-- 5. Inserir Order Items (CORRIGIDO - usar apenas valores do ENUM)
INSERT INTO order_item (order_id, menu_item_id, quantity, notes, status) VALUES
-- Order 1 - João Silva
(1, 1, 2, 'Sem gelo', 'PENDING'),
(1, 6, 1, 'Bem passado', 'PENDING'),
(1, 9, 1, NULL, 'PENDING'),

-- Order 2 - Maria Santos
(2, 2, 1, NULL, 'PENDING'),  -- CORRIGIDO: era 'COMPLETED'
(2, 7, 2, 'Um mal passado, um ao ponto', 'PENDING'),  -- CORRIGIDO: era 'PREPARING'
(2, 10, 2, NULL, 'PENDING'),

-- Order 3 - Carlos Oliveira
(3, 4, 1, 'Extra queijo', 'PENDING'),
(3, 8, 1, 'Sem gluten', 'PENDING');

-- 6. Inserir Payments (CORRIGIDO - usar apenas valores do ENUM)
INSERT INTO payment (order_id, amount, method, status) VALUES
(1, 97.00, 'CREDIT_CARD', 'COMPLETED'),
(2, 159.00, 'PIX', 'PENDING'),
(3, 47.00, 'CASH', 'COMPLETED');