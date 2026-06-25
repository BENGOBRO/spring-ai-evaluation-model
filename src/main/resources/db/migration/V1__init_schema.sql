-- Схема БД интернет-магазина электроники (PostgreSQL, Spring Data JDBC).

CREATE TABLE users (
    id    BIGSERIAL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(32)
);

CREATE TABLE product (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255)  NOT NULL,
    category        VARCHAR(64)   NOT NULL,
    brand           VARCHAR(128),
    price           NUMERIC(12,2) NOT NULL,
    memory_gb       INTEGER,
    screen_diagonal NUMERIC(4,1),
    color           VARCHAR(64),
    stock_quantity  INTEGER       NOT NULL DEFAULT 0
);

CREATE TABLE orders (
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT        NOT NULL REFERENCES users(id),
    status       VARCHAR(32)   NOT NULL DEFAULT 'NEW',
    total_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
    created_at   TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE TABLE order_item (
    id                BIGSERIAL PRIMARY KEY,
    order_id          BIGINT        NOT NULL REFERENCES orders(id),
    product_id        BIGINT        NOT NULL REFERENCES product(id),
    quantity          INTEGER       NOT NULL,
    price_at_purchase NUMERIC(12,2) NOT NULL
);

CREATE TABLE cart (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id)
);

CREATE TABLE cart_item (
    id         BIGSERIAL PRIMARY KEY,
    cart_id    BIGINT  NOT NULL REFERENCES cart(id),
    product_id BIGINT  NOT NULL REFERENCES product(id),
    quantity   INTEGER NOT NULL
);

CREATE TABLE payment (
    id       BIGSERIAL PRIMARY KEY,
    order_id BIGINT        NOT NULL REFERENCES orders(id),
    amount   NUMERIC(12,2) NOT NULL,
    status   VARCHAR(32)   NOT NULL DEFAULT 'PENDING',
    method   VARCHAR(32),
    paid_at  TIMESTAMP
);
