CREATE TABLE "users" (
 "id" bigserial PRIMARY KEY,
 "email" text,
 "hash_password" text,
 "login" text,
 "description" text,
 "photo_data" BYTEA,
 UNIQUE(email)
);

CREATE TABLE "tasks"
(
    "id"             bigserial PRIMARY KEY,
    "name"           text,
    "creator_id"     bigint,
    "description"    text,
    "status"         text,
    "created_at"     timestamp,
    "involved_count" int,
    "x_coord"        real,
    "y_coord"        real,
    UNIQUE (name, creator_id)
);

CREATE TABLE "task_hashtags" (
 "value" text,
 "task_id" bigint,
 UNIQUE('value', 'task_id')
);

CREATE TABLE "taken_tasks" (
 "user_id" bigint,
 "task_id" bigint,
 UNIQUE("user_id", "task_id")
);


CREATE TABLE "likes" (
 "id" bigserial PRIMARY KEY,
 "user_id_to" bigint,
 "task_id" bigint,
 "message" text,
 "created_at" timestamp
 UNIQUE(user_id_to, task_id)
);

CREATE TABLE "pushes" (
 "id" bigserial PRIMARY KEY,
 "user_id_to" bigint,
 "task_id_for" bigint,
 "message" text,
 "created_at" timestamp
);


------


CREATE TABLE "task_photos" (
 “id” bigserial PRIMARY KEY,
 "task_id" bigint,
 "photo_url" text
);


CREATE TABLE "comments" (
 "id" bigserial PRIMARY KEY,
 "author_id" bigint,
 "text" text,
 "task_id" bigint,
 "created_at" timestamp
);


CREATE TABLE "comment_photos" (
 “id” bigserial PRIMARY KEY,
 "comment_id" bigint,
 "photo_url" text
);
