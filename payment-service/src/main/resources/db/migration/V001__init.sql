CREATE TABLE IF NOT EXISTS payments
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subscriber_id   UUID           NOT NULL,
    subscription_id UUID           NOT NULL,
    amount          DECIMAL(10, 2) NOT NULL,
    status          VARCHAR(20)    NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED')),
    created_at      TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP      NOT NULL DEFAULT NOW()
);

