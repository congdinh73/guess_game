-- V1__Create_users_table.sql
-- Create users table

CREATE TABLE IF NOT EXISTS users
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    username
    VARCHAR
(
    50
) NOT NULL UNIQUE,
    password VARCHAR
(
    255
) NOT NULL,
    email VARCHAR
(
    100
) NOT NULL,
    score INTEGER NOT NULL DEFAULT 0,
    turns INTEGER NOT NULL DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Create indexes
CREATE INDEX idx_username ON users (username);
CREATE INDEX idx_score ON users (score DESC);


