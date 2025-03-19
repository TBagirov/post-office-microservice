-- Таблица для отчетов по подписчикам
CREATE TABLE report_subscriber
(
    subscriber_id UUID PRIMARY KEY,
    user_id     UUID       NOT NULL UNIQUE,
    username      VARCHAR(255) NOT NULL,
    name        VARCHAR(50) NOT NULL,
    surname     VARCHAR(50) NOT NULL,
    patronymic  VARCHAR(50) NOT NULL
);

-- Таблица для отчетов по публикациям (изданиям)
CREATE TABLE report_publication
(
    publication_id   UUID PRIMARY KEY,
    index            VARCHAR(17)    NOT NULL UNIQUE,
    author           VARCHAR(255)   NOT NULL,
    title            VARCHAR(255)   NOT NULL,
    type             VARCHAR(255)   NOT NULL,
    price            DECIMAL(10, 2) NOT NULL,
    count_subscriber INT            NOT NULL DEFAULT 0
);

-- Таблица для всех подписок с деталями о подписчике
CREATE TABLE report_subscription
(
    subscription_id         UUID PRIMARY KEY,
    subscriber_id           UUID        NOT NULL,
    publication_id          UUID        NOT NULL,
    start_date_subscription TIMESTAMP   NOT NULL,
    end_date_subscription   TIMESTAMP   NOT NULL,
    status                  VARCHAR(25) NOT NULL CHECK (status IN ('PENDING_PAYMENT', 'ACTIVE', 'CANCELLED', 'EXPIRED')),
    FOREIGN KEY (subscriber_id) REFERENCES report_subscriber (subscriber_id) ON DELETE CASCADE,
    FOREIGN KEY (publication_id) REFERENCES report_publication (publication_id) ON DELETE CASCADE
);
