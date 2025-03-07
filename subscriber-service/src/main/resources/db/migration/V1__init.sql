-- Создание таблицы подписчиков
CREATE TABLE IF NOT EXISTS subscribers
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID       NOT NULL UNIQUE,
    district_id UUID       NULL,
    street_id   UUID       NULL,
    building    VARCHAR(5) NOT NULL,
    sub_address VARCHAR(5),
    created_at  TIMESTAMP,
    updated_at TIMESTAMP
);
