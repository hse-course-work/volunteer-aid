CREATE TABLE "likes" (
 "id" bigserial PRIMARY KEY,
 "user_id_to" bigint,
 "task_id" bigint,
 "created_at" timestamp,
 UNIQUE(user_id_to, task_id)
);