-- таблица подписок подписчиков на издания
CREATE TABLE IF NOT EXISTS subscriptions
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subscriber_id  UUID      NOT NULL,
    publication_id UUID      NOT NULL,
    start_date     TIMESTAMP NOT NULL,
    duration       INT  NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP NULL,
    CONSTRAINT unique_subscription UNIQUE (subscriber_id, publication_id) -- Ограничение на уникальность
);


