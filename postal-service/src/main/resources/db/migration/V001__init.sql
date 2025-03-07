-- Создание таблицы почтальонов
CREATE TABLE IF NOT EXISTS postmans
(
    id      UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE -- Ссылка на пользователя без FOREIGN KEY
);

-- Создание таблицы участков (регионы)
CREATE TABLE IF NOT EXISTS regions
(
    id   UUID        NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(40) NOT NULL UNIQUE
);

-- Создание таблицы улиц
CREATE TABLE IF NOT EXISTS streets
(
    id        UUID        NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    name      VARCHAR(50) NOT NULL UNIQUE,
    region_id UUID NULL -- Ссылка на регион без FOREIGN KEY
);

-- Создание таблицы районов (участки)
CREATE TABLE IF NOT EXISTS districts
(
    id         UUID NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    postman_id UUID NULL, -- Ссылка на почтальона без FOREIGN KEY
    region_id  UUID NULL, -- Ссылка на регион без FOREIGN KEY
    CONSTRAINT unique_postman_region UNIQUE (postman_id, region_id)
);
