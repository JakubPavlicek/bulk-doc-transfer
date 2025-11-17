-- Sequences for automatic ID generation
CREATE SEQUENCE IF NOT EXISTS submitters_id_seq;
CREATE SEQUENCE IF NOT EXISTS files_id_seq;
CREATE SEQUENCE IF NOT EXISTS document_submissions_id_seq;
CREATE SEQUENCE IF NOT EXISTS submission_state_history_id_seq;

-- Tables
CREATE TABLE IF NOT EXISTS "submitters" (
  "id" bigint PRIMARY KEY DEFAULT nextval('submitters_id_seq'),
  "email" varchar(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS "files" (
  "id" bigint PRIMARY KEY DEFAULT nextval('files_id_seq'),
  "submission_id" bigint NOT NULL,
  "name" varchar(255) NOT NULL,
  "type" varchar(150) NOT NULL,
  "size" bigint NOT NULL,
  "content" bytea,
  "created_at" timestamp
);

CREATE TABLE IF NOT EXISTS "document_submissions" (
  "id" bigint PRIMARY KEY DEFAULT nextval('document_submissions_id_seq'),
  "submitter_id" bigint NOT NULL,
  "subject" varchar(2000) NOT NULL,
  "description" text,
  "reference_number" varchar(255) UNIQUE NOT NULL,
  "created_at" timestamp,
  "saved_at" timestamp,
  "state" varchar(13) check (state in ('ACCEPTED', 'PROCESSED', 'SAVED', 'RESPONSE_SENT')) NOT NULL,
  "check_result" varchar(20) check (check_result in ('OK', 'ELECTRONIC_SIGNATURE', 'MALWARE')),
  "total_files" int
);

CREATE TABLE IF NOT EXISTS "submission_state_history" (
  "id" bigint PRIMARY KEY DEFAULT nextval('submission_state_history_id_seq'),
  "submission_id" bigint NOT NULL,
  "current_state" varchar(13) check (current_state in ('ACCEPTED', 'PROCESSED', 'SAVED', 'RESPONSE_SENT')) NOT NULL,
  "changed_at" timestamp
);

-- Add named foreign key constraints
ALTER TABLE "files" ADD CONSTRAINT "fk_files_submission" FOREIGN KEY ("submission_id") REFERENCES "document_submissions" ("id");

ALTER TABLE "document_submissions" ADD CONSTRAINT "fk_submissions_submitter" FOREIGN KEY ("submitter_id") REFERENCES "submitters" ("id");

ALTER TABLE "submission_state_history" ADD CONSTRAINT "fk_state_history_submission" FOREIGN KEY ("submission_id") REFERENCES "document_submissions" ("id");
