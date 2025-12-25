-- Сессии
CREATE TABLE sessions (
    session_id VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(255) NOT NULL,
    user_agent VARCHAR(1024) NOT NULL,
    active BOOLEAN DEFAULT FALSE
);