CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    profile_description TEXT
);
