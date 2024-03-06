CREATE TABLE "users" (
 "id" bigserial PRIMARY KEY,
 "email" text,
 "hash_password" text,
 "login" text,
 "description" text,
 "photo_url" text
 UNIQUE(email)
);


CREATE TABLE "tasks" (
 "id" bigserial PRIMARY KEY,
 "creator_id" bigint,
 "description" text,
 "status_id" int,
 "created_at" timestamp,
 "involved_count" int,
  "x_coord" real,
  "y_coord" real,
);


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


CREATE TABLE "task_hashtags" (
 "hashtag_id" int,
 "task_id" bigint
);


CREATE TABLE "hashtags" (
 "id" serial PRIMARY KEY,
 "value" text
);


CREATE TABLE "task_statuses" (
 "id" serial PRIMARY KEY,
 "value" text
);


CREATE TABLE "personal_tasks" (
 "user_id" bigint,
 "task_id" bigint
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
 "commen_id" bigint,
 "message" text,
 "created_at" timestamp
);



ALTER TABLE "task_photos" ADD FOREIGN KEY ("task_id") REFERENCES "tasks" ("id");


ALTER TABLE "comment_photos" ADD FOREIGN KEY ("comment_id") REFERENCES "comments" ("id");


ALTER TABLE "comments" ADD FOREIGN KEY ("author_id") REFERENCES "users" ("id");


ALTER TABLE "comments" ADD FOREIGN KEY ("task_id") REFERENCES "tasks" ("id");


ALTER TABLE "tasks" ADD FOREIGN KEY ("creator_id") REFERENCES "users" ("id");


ALTER TABLE "task_hashtags" ADD FOREIGN KEY ("task_id") REFERENCES "tasks" ("id");


ALTER TABLE "task_hashtags" ADD FOREIGN KEY ("hashtag_id") REFERENCES "hashtags" ("id");


ALTER TABLE "tasks" ADD FOREIGN KEY ("status_id") REFERENCES "task_statuses" ("id");


ALTER TABLE "personal_tasks" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("id");


ALTER TABLE "personal_tasks" ADD FOREIGN KEY ("task_id") REFERENCES "tasks" ("id");


ALTER TABLE "likes" ADD FOREIGN KEY ("user_id_to") REFERENCES "users" ("id");


ALTER TABLE "likes" ADD FOREIGN KEY ("task_id") REFERENCES "tasks" ("id");


ALTER TABLE "pushes" ADD FOREIGN KEY ("user_id_to") REFERENCES "users" ("id");


ALTER TABLE "pushes" ADD FOREIGN KEY ("user_id_to") REFERENCES "comments" ("id");

