ALTER TABLE subscriptions DROP CONSTRAINT unique_subscription;

CREATE UNIQUE INDEX unique_active_subscription
    ON subscriptions (subscriber_id, publication_id)
    WHERE status = 'ACTIVE';
