CREATE TABLE IF NOT EXISTS users (
    id bigserial PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    hash_password VARCHAR(255) NOT NULL,
    profile_description TEXT,
    login VARCHAR(255) NOT NULL,
    photo_url VARCHAR(255) NULL
);
