-- Тестовые данные: пользователь, банки, категории расходов и доходов, а также транзакции

-- 1. Тестовый пользователь
INSERT INTO users (id, username, password_hash, role)
SELECT
  gen_random_uuid(),
  'user',                             -- имя для входа в тестах
  crypt('password', gen_salt('bf')),  -- пароль: password
  'ROLE_USER'
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE username = 'user'
);

-- 2. Тестовые банки
INSERT INTO bank (id, name)
SELECT gen_random_uuid(), 'Bank A'
WHERE NOT EXISTS (SELECT 1 FROM bank WHERE name = 'Bank A');
INSERT INTO bank (id, name)
SELECT gen_random_uuid(), 'Bank B'
WHERE NOT EXISTS (SELECT 1 FROM bank WHERE name = 'Bank B');
INSERT INTO bank (id, name)
SELECT gen_random_uuid(), 'Bank C'
WHERE NOT EXISTS (SELECT 1 FROM bank WHERE name = 'Bank C');

-- 3. Категории расходов
INSERT INTO category (id, name)
SELECT gen_random_uuid(), 'Food'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Food');
INSERT INTO category (id, name)
SELECT gen_random_uuid(), 'Utilities'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Utilities');
INSERT INTO category (id, name)
SELECT gen_random_uuid(), 'Entertainment'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Entertainment');

-- 4. Категории доходов
INSERT INTO category (id, name)
SELECT gen_random_uuid(), 'Salary'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Salary');
INSERT INTO category (id, name)
SELECT gen_random_uuid(), 'Bonus'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Bonus');
INSERT INTO category (id, name)
SELECT gen_random_uuid(), 'Other'
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Other');

-- 5. Генерация транзакций (1000 шт.) для пользователя 'user'
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
    now() - (floor(random() * 365 * 3)::int * interval '1 day'),
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
    CASE
      WHEN tt.name = 'Credit' THEN 'Income transaction ' || gs
      ELSE 'Expense transaction ' || gs
    END
FROM generate_series(1, 1000) AS gs
CROSS JOIN LATERAL (
  SELECT id FROM users WHERE username = 'user' LIMIT 1
) u
CROSS JOIN LATERAL (
  SELECT id FROM party_type ORDER BY name
  OFFSET (gs % 2) LIMIT 1
) pt
CROSS JOIN LATERAL (
  SELECT id, name FROM transaction_type ORDER BY name
  OFFSET (gs % 2) LIMIT 1
) tt(id, name)
CROSS JOIN LATERAL (
  SELECT id FROM status WHERE name = 'New' LIMIT 1
) st
CROSS JOIN LATERAL (
  SELECT id FROM bank ORDER BY name
  OFFSET (gs % 3) LIMIT 1
) bs
CROSS JOIN LATERAL (
  SELECT id FROM bank ORDER BY name
  OFFSET ((gs + 1) % 3) LIMIT 1
) br
CROSS JOIN LATERAL (
  SELECT id
  FROM category
  WHERE (tt.name = 'Credit' AND name IN ('Salary','Bonus','Other'))
     OR (tt.name = 'Debit'  AND name IN ('Food','Utilities','Entertainment'))
  ORDER BY name
  OFFSET (gs % 3) LIMIT 1
) c;
