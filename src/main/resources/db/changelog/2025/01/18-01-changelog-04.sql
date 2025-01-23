-- liquibase formatted sql

-- changeset a_bibik:1737374477-1
ALTER TABLE account
    ADD archive_date TIMESTAMP;
ALTER TABLE transaction
    ADD archive_date TIMESTAMP;
ALTER TABLE data_source_error_log
    ADD archive_date TIMESTAMP;