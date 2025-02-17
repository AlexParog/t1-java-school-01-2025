-- liquibase formatted sql

-- changeset a_bibik:1739109927-1
ALTER TABLE client
    ADD COLUMN client_id UUID NOT NULL DEFAULT gen_random_uuid();