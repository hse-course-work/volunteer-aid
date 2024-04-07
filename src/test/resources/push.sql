CREATE TABLE "pushes" (
 "id" bigserial PRIMARY KEY,
 "user_id_to" bigint,
 "task_id_for" bigint,
 "message" text,
 "created_at" timestamp
);