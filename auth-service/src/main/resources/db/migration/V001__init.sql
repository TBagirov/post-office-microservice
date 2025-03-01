-- Таблица ролей
CREATE TABLE IF NOT EXISTS roles
(
    id   UUID        NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE
    );

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users
(
    id          UUID        NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(50) NOT NULL,
    surname     VARCHAR(50) NOT NULL,
    patronymic  VARCHAR(50) NOT NULL,
    username    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(100) UNIQUE,
    phone       VARCHAR(15) UNIQUE,
    password    VARCHAR(512) NOT NULL,
    created_at  TIMESTAMP   NOT NULL,
    role_id     UUID        NOT NULL REFERENCES roles (id) ON DELETE SET NULL
    );

-- Таблица refresh-токенов
CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id      UUID    NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID    NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token   TEXT    NOT NULL UNIQUE
    );
