-- Таблица типов изданий (например, газеты, журналы, книги и т.д.)
CREATE TABLE IF NOT EXISTS publication_types
(
    id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Таблица изданий
CREATE TABLE IF NOT EXISTS publications
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    index       VARCHAR(17)    NOT NULL UNIQUE,
    author      VARCHAR(255)   NOT NULL,
    title       VARCHAR(255)   NOT NULL,
    description TEXT           NULL,
    price       DECIMAL(10, 2) NOT NULL,
    type_id     UUID           NOT NULL REFERENCES publication_types (id) ON DELETE CASCADE
);
