-- liquibase formatted sql

-- changeset a_bibik:1739109912-1
ALTER TABLE transaction
    ADD COLUMN transaction_id UUID NOT NULL DEFAULT gen_random_uuid(),
    ADD COLUMN status         VARCHAR(50);