-- src/main/resources/db/migration/V1__Init.sql

-- 1. Справочники
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE party_type (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE transaction_type (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE status (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE bank (
    id UUID PRIMARY KEY,
    name VARCHAR(200) NOT NULL
);

CREATE TABLE category (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 2. Основная таблица транзакций
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    created_by_user_id UUID NOT NULL REFERENCES users(id),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    party_type_id UUID NOT NULL REFERENCES party_type(id),
    transaction_type_id UUID NOT NULL REFERENCES transaction_type(id),
    status_id UUID NOT NULL REFERENCES status(id),
    bank_sender_id UUID NOT NULL REFERENCES bank(id),
    bank_receiver_id UUID NOT NULL REFERENCES bank(id),
    account_sender VARCHAR(34) NOT NULL,
    account_receiver VARCHAR(34) NOT NULL,
    category_id UUID NOT NULL REFERENCES category(id),
    amount NUMERIC(18,5) NOT NULL,
    receiver_tin VARCHAR(11) NOT NULL,
    receiver_phone VARCHAR(12) NOT NULL,
    comment TEXT
);

-- 3. Таблица аудита изменений
CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    entity_name VARCHAR(100) NOT NULL,
    entity_id UUID NOT NULL,
    changed_by_user_id UUID NOT NULL REFERENCES users(id),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    changes TEXT NOT NULL
);

-- 4. Индексы для фильтрации
CREATE INDEX idx_tx_timestamp       ON transactions(timestamp);
CREATE INDEX idx_tx_status          ON transactions(status_id);
CREATE INDEX idx_tx_bank_sender     ON transactions(bank_sender_id);
CREATE INDEX idx_tx_bank_receiver   ON transactions(bank_receiver_id);
CREATE INDEX idx_tx_category        ON transactions(category_id);
CREATE INDEX idx_tx_tin             ON transactions(receiver_tin);
CREATE INDEX idx_tx_amount          ON transactions(amount);

-- 5. Расширение для UUID
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 6. Заполнение справочников
INSERT INTO party_type (id, name) VALUES
  (gen_random_uuid(), 'Physical'),
  (gen_random_uuid(), 'Legal');

INSERT INTO transaction_type (id, name) VALUES
  (gen_random_uuid(), 'Credit'),
  (gen_random_uuid(), 'Debit');

INSERT INTO status (id, name) VALUES
  (gen_random_uuid(), 'New'),
  (gen_random_uuid(), 'Confirmed'),
  (gen_random_uuid(), 'Processing'),
  (gen_random_uuid(), 'Cancelled'),
  (gen_random_uuid(), 'Completed'),
  (gen_random_uuid(), 'Deleted'),
  (gen_random_uuid(), 'Returned');
