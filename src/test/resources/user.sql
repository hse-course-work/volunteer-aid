CREATE TABLE "users" (
 "id" bigserial PRIMARY KEY,
 "email" text,
 "hash_password" text,
 "login" text,
 "description" text,
 "photo_data" BYTEA,
 UNIQUE(email)
);