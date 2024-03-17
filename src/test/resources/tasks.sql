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
