-- V4__uppercase_transaction_types.sql
-- Приводим в верхний регистр значения типа транзакций «Credit» и «Debit»

UPDATE transaction_type
SET name = 'CREDIT'
WHERE LOWER(name) = 'credit';

UPDATE transaction_type
SET name = 'DEBIT'
WHERE LOWER(name) = 'debit';
