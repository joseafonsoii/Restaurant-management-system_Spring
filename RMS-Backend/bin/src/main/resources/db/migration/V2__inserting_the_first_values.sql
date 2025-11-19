-- ================================================================================
-- Categries
-- ================================================================================

INSERT INTO categories (name,description,display_order) VALUES
('Appetizers', 'Starters and small plates to beginyour meal',1),
('Main Courses', 'Complete meals and main dishes', 2),
('Desserts', 'Sweets and desserts to finish your meal',3),
('Beverages','Drinks,juices and refreshments',4),
('Sides','Additional sides and extras',5);

-- ================================================================================
-- Menu items
-- ================================================================================
INSERT INTO menu_items (name, description, display_order,price,category_id,preparation_time,ingredients,tags) VALUES
('Coxinha','Traditional Brazilian chicken croquette',8.90,1,10,
'{"chicken","cream cheese","wheat flour","seasonings"}','{"popular","traditional","äppetizer"}'),
('Feijoada','Traditional Brazilian black bean stew with pork',32.90,1,12,
'{"black beans", "dried meat","Calabresa sausage","pork ribs","colard greens","cassava"flour}',
'{"traditional","brazillian","main course"}'),
('Chocolate Mousse','Creamy Belgian chocolate mousse',12.50,3,15,
'{"chocolate","heavy cream","sugar","egg withes"}','{"dessert","chocolate","sweet"}'),
('Coca Cola 350ml','Coca-Cola soda can 350ml',6.90,4,1,
'{}','{"soda","cold","refreshing"}');

-- ================================================================================
-- Menu items
-- ================================================================================
INSERT INTO restaurant_tables(table_number, capacity,status) VALUES
('T-01',4,'AVAILABLE'),
('T-02',6,'AVAILABLE'),
('T-03',2,'AVAILABLE'),
('T-04',8,'AVAILABLE'),
('T-05',4,'AVAILABLE');

