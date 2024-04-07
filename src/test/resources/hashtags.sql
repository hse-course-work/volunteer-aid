CREATE TABLE "task_hashtags" (
 "value" text,
 "task_id" bigint,
 UNIQUE('value', 'task_id')
);
