-- liquibase formatted sql

-- changeset a_bibik:1739109936-1
ALTER TABLE account
    ADD COLUMN account_id    UUID           NOT NULL DEFAULT gen_random_uuid(),
    ADD COLUMN status        VARCHAR(50),
    ADD COLUMN frozen_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00;