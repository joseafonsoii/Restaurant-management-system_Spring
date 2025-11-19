DELIMITER //

-- Trigger para category
CREATE TRIGGER before_category_update
    BEFORE UPDATE ON category
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

-- Trigger para menu_item
CREATE TRIGGER before_menu_item_update
    BEFORE UPDATE ON menu_item
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

-- Trigger para restaurant_table
CREATE TRIGGER before_restaurant_table_update
    BEFORE UPDATE ON restaurant_table
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

-- Trigger para orders
CREATE TRIGGER before_orders_update
    BEFORE UPDATE ON orders
    FOR EACH ROW
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//

DELIMITER ;