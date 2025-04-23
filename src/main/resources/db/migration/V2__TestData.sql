-- src/main/resources/db/migration/V2__TestData.sql
-- Тестовые данные: пользователь, банки, категории и транзакции

-- 1. Тестовый пользователь
INSERT INTO users (id, username, password_hash, role)
VALUES (
  gen_random_uuid(),
  'testuser',
  crypt('password', gen_salt('bf')),  -- пароль: password
  'ROLE_USER'
);

-- 2. Тестовые банки
INSERT INTO bank (id, name) VALUES
  (gen_random_uuid(), 'Bank A'),
  (gen_random_uuid(), 'Bank B'),
  (gen_random_uuid(), 'Bank C');

-- 3. Тестовые категории
INSERT INTO category (id, name) VALUES
  (gen_random_uuid(), 'Food'),
  (gen_random_uuid(), 'Utilities'),
  (gen_random_uuid(), 'Entertainment');

-- 4. Пара десятков транзакций
INSERT INTO transactions (
    id,
    created_by_user_id,
    timestamp,
    party_type_id,
    transaction_type_id,
    status_id,
    bank_sender_id,
    bank_receiver_id,
    account_sender,
    account_receiver,
    category_id,
    amount,
    receiver_tin,
    receiver_phone,
    comment
)
SELECT
    gen_random_uuid(),
    u.id,
    now() - (random() * interval '30 days'),
    pt.id,
    tt.id,
    st.id,
    bs.id,
    br.id,
    'ACC' || lpad((100000 + floor(random() * 900000))::text, 6, '0'),
    'ACC' || lpad((100000 + floor(random() * 900000))::text, 6, '0'),
    c.id,
    round((10 + random() * 990)::numeric, 2),
    lpad(floor(random() * 10000000000)::text, 11, '0'),
    '+7' || lpad(floor(random() * 1000000000)::text, 10, '0'),
    'Auto-generated transaction ' || gs
FROM generate_series(1,100) AS gs
CROSS JOIN LATERAL (SELECT id FROM users WHERE username = 'testuser' LIMIT 1) u
CROSS JOIN LATERAL (
  SELECT id FROM party_type ORDER BY name
  OFFSET (gs % 2) LIMIT 1
) pt
CROSS JOIN LATERAL (
  SELECT id FROM transaction_type ORDER BY name
  OFFSET (gs % 2) LIMIT 1
) tt
CROSS JOIN LATERAL (
  SELECT id FROM status WHERE name = 'New' LIMIT 1
) st
CROSS JOIN LATERAL (
  SELECT id FROM bank ORDER BY name
  OFFSET (gs % 3) LIMIT 1
) bs  -- bank sender
CROSS JOIN LATERAL (
  SELECT id FROM bank ORDER BY name
  OFFSET ((gs + 1) % 3) LIMIT 1
) br  -- bank receiver
CROSS JOIN LATERAL (
  SELECT id FROM category ORDER BY name
  OFFSET (gs % 3) LIMIT 1
) c;
