\connect cart_service_db cart_service_user
-- Create the 'carts' table
CREATE TABLE carts (
                       user_id BIGINT PRIMARY KEY,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create the 'cart_items' table
CREATE TABLE cart_items (
                            id BIGSERIAL PRIMARY KEY,
                            cart_id BIGINT NOT NULL REFERENCES carts(user_id) ON DELETE CASCADE,
                            product_id BIGINT NOT NULL,
                            quantity INTEGER NOT NULL CHECK (quantity > 0),
                            UNIQUE (cart_id, product_id)
);




-- Note: product_id references products in the Product Service; adjust IDs as necessary.
