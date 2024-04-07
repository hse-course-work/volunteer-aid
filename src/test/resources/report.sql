CREATE TABLE "reports" (
 "id" bigserial PRIMARY KEY,
 "task_id_for" bigint,
 "author_id" bigint,
 "comment" text,
 "photo_data" text
);