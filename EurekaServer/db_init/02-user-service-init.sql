\connect user_service_db user_service_user

-- Create the 'users' table
CREATE TABLE users (
                       user_id BIGSERIAL PRIMARY KEY,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       phone_number VARCHAR(11) UNIQUE NOT NULL,
                       image_url VARCHAR(255),
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Unique constraints (if not already applied in the table definition)
ALTER TABLE users
    ADD CONSTRAINT unique_email UNIQUE (email),
    ADD CONSTRAINT unique_phone UNIQUE (phone_number);

-- Insert sample users
-- password: password1235
INSERT INTO users (full_name, email, password, address, phone_number, image_url, role)
VALUES
    ('John Weak', 'john@example.com', '$2a$10$YMFLNO48yWgifv.x43peu.RHja4EGMz81dzITPvgRcUp1mxTIxXPG', '123 Main St', '0909912345', 'https://images.pexels.com/photos/27045943/pexels-photo-27045943/free-photo-of-young-man-wearing-wrinkled-white-shirt-standing-with-hands-on-hips.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1', 'ROLE_USER'),
    ('Test User', 'testuser@gmail.com', '$2a$10$YMFLNO48yWgifv.x43peu.RHja4EGMz81dzITPvgRcUp1mxTIxXPG', '123 Main St', '0909954321', 'http://example.com/john.jpg', 'ROLE_USER'),
    ('Ultimate Store', 'jane@example.com', '$2a$10$YMFLNO48yWgifv.x43peu.RHja4EGMz81dzITPvgRcUp1mxTIxXPG', '456 Elm St', '0912345678', 'https://cdn.pixabay.com/photo/2019/04/26/07/14/store-4156934_1280.png', 'ROLE_VENDOR'),
    ('Zenith Shop', 'testvendor@gmail.com', '$2a$10$YMFLNO48yWgifv.x43peu.RHja4EGMz81dzITPvgRcUp1mxTIxXPG', '456 Elm St', '0987654322', 'http://example.com/jane.jpg', 'ROLE_VENDOR');
