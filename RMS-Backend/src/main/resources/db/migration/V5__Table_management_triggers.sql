DELIMITER //

-- Quando um pedido é criado, ocupar a mesa
CREATE TRIGGER after_order_insert
    AFTER INSERT ON orders
    FOR EACH ROW
BEGIN
    UPDATE restaurant_table
    SET status = 'OCCUPIED'
    WHERE id = NEW.table_id
    AND status = 'AVAILABLE';
END//

-- Quando um pedido é pago ou cancelado, liberar a mesa
CREATE TRIGGER after_order_status_update
    AFTER UPDATE ON orders
    FOR EACH ROW
BEGIN
    IF NEW.status IN ('PAID', 'CANCELLED') AND OLD.status NOT IN ('PAID', 'CANCELLED') THEN
        UPDATE restaurant_table
        SET status = 'AVAILABLE'
        WHERE id = NEW.table_id;
    END IF;
END//

DELIMITER ;