-- V2__Insert_sample_users.sql
-- Insert 20 sample users for leaderboard testing

INSERT INTO users (username, password, email, score, turns)
VALUES ('champion', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 150, 5),
       ('pro_gamer', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 142, 8),
       ('lucky_star', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 135, 3),
       ('mastermind', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 128, 10),
       ('wizard123', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 121, 7),
       ('ninja_player', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 115, 2),
       ('speed_demon', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 108, 6),
       ('brain_power', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 102, 4),
       ('fortune_seeker', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 95, 9),
       ('number_wizard', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 88, 5),
       ('casual_player', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 72, 5),
       ('weekend_warrior', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 65, 3),
       ('lucky_guesser', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 58, 8),
       ('strategy_king', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 51, 2),
       ('number_hunter', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 44, 7),
       ('newbie2024', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 28, 10),
       ('first_timer', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 15, 5),
       ('learning_fast', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 9, 6),
       ('rookie_player', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 3, 5),
       ('just_started', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '[email protected]', 0, 5);

-- Reset sequence after manual inserts
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- Update timestamps
UPDATE users
SET created_at = CURRENT_TIMESTAMP - INTERVAL '30 days'
WHERE username IN ('champion', 'pro_gamer');
UPDATE users
SET created_at = CURRENT_TIMESTAMP - INTERVAL '20 days'
WHERE username IN ('lucky_star', 'mastermind');
UPDATE users
SET created_at = CURRENT_TIMESTAMP - INTERVAL '10 days'
WHERE username IN ('wizard123', 'ninja_player');
UPDATE users
SET created_at = CURRENT_TIMESTAMP - INTERVAL '5 days'
WHERE username IN ('newbie2024', 'first_timer');
UPDATE users
SET created_at = CURRENT_TIMESTAMP - INTERVAL '1 day'
WHERE username IN ('rookie_player', 'just_started');
