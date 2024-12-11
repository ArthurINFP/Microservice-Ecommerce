CREATE DATABASE product_service_db;
CREATE DATABASE user_service_db;
CREATE DATABASE cart_service_db;
CREATE DATABASE order_service_db;
CREATE DATABASE payment_service_db;

CREATE USER product_service_user WITH ENCRYPTED PASSWORD 'product_password';
CREATE USER user_service_user WITH ENCRYPTED PASSWORD 'user_password';
CREATE USER cart_service_user WITH ENCRYPTED PASSWORD 'cart_password';
CREATE USER order_service_user WITH ENCRYPTED PASSWORD 'order_password';
CREATE USER payment_service_user WITH ENCRYPTED PASSWORD 'payment_password';

GRANT ALL PRIVILEGES ON DATABASE product_service_db TO product_service_user;
GRANT ALL PRIVILEGES ON DATABASE user_service_db TO user_service_user;
GRANT ALL PRIVILEGES ON DATABASE cart_service_db TO cart_service_user;
GRANT ALL PRIVILEGES ON DATABASE order_service_db TO order_service_user;
GRANT ALL PRIVILEGES ON DATABASE payment_service_db TO payment_service_user;

\connect user_service_db;

GRANT USAGE ON SCHEMA public TO user_service_user;
GRANT CREATE ON SCHEMA public TO user_service_user;

-- For Product Service
\connect product_service_db;

GRANT USAGE ON SCHEMA public TO product_service_user;
GRANT CREATE ON SCHEMA public TO product_service_user;

\connect cart_service_db;

GRANT USAGE ON SCHEMA public TO cart_service_user;
GRANT CREATE ON SCHEMA public TO cart_service_user;


\connect order_service_db;

GRANT USAGE ON SCHEMA public TO order_service_user;
GRANT CREATE ON SCHEMA public TO order_service_user;


\connect payment_service_db;

GRANT USAGE ON SCHEMA public TO payment_service_user;
GRANT CREATE ON SCHEMA public TO payment_service_user;