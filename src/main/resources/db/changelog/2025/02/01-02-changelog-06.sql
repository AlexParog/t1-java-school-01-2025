-- liquibase formatted sql

-- changeset a_bibik:1739464897-1
CREATE UNIQUE INDEX idx_transaction_transaction_id ON transaction (transaction_id);

-- changeset a_bibik:1739464897-2
CREATE UNIQUE INDEX idx_client_client_id ON client (client_id);

-- changeset a_bibik:1739464897-3
CREATE UNIQUE INDEX idx_account_account_id ON account (account_id);