-- Удаляем старый уникальный индекс (V3)
DROP INDEX IF EXISTS unique_active_subscription;

-- Удаляем старый триггер, если он существовал
DROP TRIGGER IF EXISTS prevent_new_subscription_if_active_exists ON subscriptions;
DROP FUNCTION IF EXISTS check_existing_active_subscription;

-- Создаем новую функцию для валидации подписок перед вставкой
CREATE OR REPLACE FUNCTION check_existing_active_subscription()
    RETURNS TRIGGER AS $$
BEGIN
    -- Проверяем, есть ли уже подписка с таким же subscriber_id и publication_id в статусе ACTIVE
    IF EXISTS (
        SELECT 1 FROM subscriptions
        WHERE subscriber_id = NEW.subscriber_id
          AND publication_id = NEW.publication_id
          AND status = 'ACTIVE'
    ) THEN
        RAISE EXCEPTION 'Нельзя создать новую подписку: уже есть активная подписка для subscriber_id=% и publication_id=%',
            NEW.subscriber_id, NEW.publication_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создаем новый триггер для проверки перед вставкой новых подписок
CREATE TRIGGER prevent_new_subscription_if_active_exists
    BEFORE INSERT ON subscriptions
    FOR EACH ROW
EXECUTE FUNCTION check_existing_active_subscription();
