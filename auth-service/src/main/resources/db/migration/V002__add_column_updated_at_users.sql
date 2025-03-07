-- Добавление нового столбца updated_at в таблицу users
ALTER TABLE users
    ADD COLUMN updated_at TIMESTAMP NULL