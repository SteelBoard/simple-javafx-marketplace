CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


CREATE TRIGGER update_products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


CREATE TRIGGER update_carts_updated_at
    BEFORE UPDATE ON carts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


CREATE TRIGGER update_orders_updated_at
    BEFORE UPDATE ON orders
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();


CREATE OR REPLACE FUNCTION promote_to_seller_function()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE users
    SET role = 'SELLER'
    WHERE id = NEW.seller_id AND role = 'USER';
    RAISE NOTICE 'Пользователь % повышен до SELLER', NEW.seller_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS promote_to_seller_trigger ON products;
CREATE TRIGGER promote_to_seller_trigger
    AFTER INSERT ON products
    FOR EACH ROW
    EXECUTE FUNCTION promote_to_seller_function();


CREATE OR REPLACE FUNCTION check_seller_demotion_function()
RETURNS TRIGGER AS $$
DECLARE
    active_products_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO active_products_count
    FROM products
    WHERE seller_id = OLD.seller_id AND active = true;

    IF active_products_count = 0 THEN
        UPDATE users
        SET role = 'USER'
        WHERE id = OLD.seller_id AND role = 'SELLER';
        RAISE NOTICE 'Пользователь % понижен до USER', OLD.seller_id;
    END IF;

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;


DROP TRIGGER IF EXISTS check_demotion_on_delete_trigger ON products;
CREATE TRIGGER check_demotion_on_delete_trigger
    AFTER DELETE ON products
    FOR EACH ROW
    EXECUTE FUNCTION check_seller_demotion_function();


DROP TRIGGER IF EXISTS check_demotion_on_update_trigger ON products;
CREATE TRIGGER check_demotion_on_update_trigger
    AFTER UPDATE ON products
    WHEN (OLD.active = true AND NEW.active = false)
    FOR EACH ROW
    EXECUTE FUNCTION check_seller_demotion_function();
