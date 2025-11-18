DELIMITER //

CREATE TRIGGER after_order_item_change
    AFTER INSERT ON order_item
    FOR EACH ROW
BEGIN
    UPDATE orders
    SET total_amount = (
        SELECT SUM(oi.quantity * mi.price)
        FROM order_item oi
        JOIN menu_item mi ON oi.menu_item_id = mi.id
        WHERE oi.order_id = NEW.order_id
    )
    WHERE id = NEW.order_id;
END//

CREATE TRIGGER after_order_item_update
    AFTER UPDATE ON order_item
    FOR EACH ROW
BEGIN
    UPDATE orders
    SET total_amount = (
        SELECT SUM(oi.quantity * mi.price)
        FROM order_item oi
        JOIN menu_item mi ON oi.menu_item_id = mi.id
        WHERE oi.order_id = NEW.order_id
    )
    WHERE id = NEW.order_id;
END//

CREATE TRIGGER after_order_item_delete
    AFTER DELETE ON order_item
    FOR EACH ROW
BEGIN
    UPDATE orders
    SET total_amount = (
        SELECT COALESCE(SUM(oi.quantity * mi.price), 0)
        FROM order_item oi
        JOIN menu_item mi ON oi.menu_item_id = mi.id
        WHERE oi.order_id = OLD.order_id
    )
    WHERE id = OLD.order_id;
END//

DELIMITER ;