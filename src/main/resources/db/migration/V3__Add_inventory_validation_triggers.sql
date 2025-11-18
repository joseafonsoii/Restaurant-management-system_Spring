ALTER TABLE menu_item ADD COLUMN current_stock INT DEFAULT NULL;

DELIMITER //

CREATE TRIGGER before_order_item_insert
    BEFORE INSERT ON order_item
    FOR EACH ROW
BEGIN
    DECLARE item_stock INT;
    DECLARE item_available BOOLEAN;

    -- Verificar disponibilidade e estoque
    SELECT available, current_stock
    INTO item_available, item_stock
    FROM menu_item
    WHERE id = NEW.menu_item_id;

    -- Se item não está disponível
    IF item_available = FALSE THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Menu item is not available';
    END IF;

    -- Se tem controle de estoque e não tem quantidade suficiente
    IF item_stock IS NOT NULL AND item_stock < NEW.quantity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient stock for menu item';
    END IF;
END//

DELIMITER ;