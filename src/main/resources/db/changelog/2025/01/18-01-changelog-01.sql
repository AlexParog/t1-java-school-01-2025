-- liquibase formatted sql

-- changeset a_bibik:1737370293-1
CREATE SEQUENCE IF NOT EXISTS account_seq START WITH 1 INCREMENT BY 50;

-- changeset a_bibik:1737370293-2
CREATE TABLE account
(
    id           BIGINT PRIMARY KEY,
    client_id    BIGINT         NOT NULL,
    account_type VARCHAR(50)    NOT NULL,
    balance      NUMERIC(10, 2) NOT NULL DEFAULT 0
);

ALTER TABLE account
    ADD CONSTRAINT fk_client_to_account
        FOREIGN KEY (client_id) REFERENCES client (id);