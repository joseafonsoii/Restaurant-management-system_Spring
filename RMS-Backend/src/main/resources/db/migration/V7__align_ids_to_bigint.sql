-- Align id and foreign key columns to BIGINT to match the JPA entities (Long).
-- Hibernate's ddl-auto attempted these alters and left an inconsistent mix of
-- INT/BIGINT; this migration normalizes everything and stabilizes startup.

ALTER TABLE menu_item DROP FOREIGN KEY menu_item_ibfk_1;
ALTER TABLE order_item DROP FOREIGN KEY order_item_ibfk_1;
ALTER TABLE order_item DROP FOREIGN KEY order_item_ibfk_2;
ALTER TABLE orders DROP FOREIGN KEY orders_ibfk_1;
ALTER TABLE payment DROP FOREIGN KEY payment_ibfk_1;

ALTER TABLE category MODIFY id BIGINT AUTO_INCREMENT;
ALTER TABLE menu_item MODIFY id BIGINT AUTO_INCREMENT,
                      MODIFY category_id BIGINT NOT NULL;
ALTER TABLE restaurant_table MODIFY id BIGINT AUTO_INCREMENT;
ALTER TABLE orders MODIFY id BIGINT AUTO_INCREMENT,
                   MODIFY table_id BIGINT NOT NULL;
ALTER TABLE order_item MODIFY id BIGINT AUTO_INCREMENT,
                       MODIFY order_id BIGINT NOT NULL,
                       MODIFY menu_item_id BIGINT NOT NULL;
ALTER TABLE payment MODIFY id BIGINT AUTO_INCREMENT,
                    MODIFY order_id BIGINT NOT NULL;

ALTER TABLE menu_item ADD CONSTRAINT menu_item_ibfk_1
    FOREIGN KEY (category_id) REFERENCES category(id);
ALTER TABLE order_item ADD CONSTRAINT order_item_ibfk_1
    FOREIGN KEY (order_id) REFERENCES orders(id);
ALTER TABLE order_item ADD CONSTRAINT order_item_ibfk_2
    FOREIGN KEY (menu_item_id) REFERENCES menu_item(id);
ALTER TABLE orders ADD CONSTRAINT orders_ibfk_1
    FOREIGN KEY (table_id) REFERENCES restaurant_table(id);
ALTER TABLE payment ADD CONSTRAINT payment_ibfk_1
    FOREIGN KEY (order_id) REFERENCES orders(id);