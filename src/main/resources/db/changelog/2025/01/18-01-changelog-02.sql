-- liquibase formatted sql

-- changeset a_bibik:1737370360-1
CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

-- changeset a_bibik:1737370360-2
CREATE TABLE transaction
(
    id               BIGINT PRIMARY KEY,
    account_id       BIGINT         NOT NULL,
    amount           NUMERIC(10, 2) NOT NULL,
    time_of_purchase TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE transaction
    ADD CONSTRAINT fk_account_to_transaction
        FOREIGN KEY (account_id) REFERENCES account (id);