-- liquibase formatted sql

-- changeset a_bibik:1737370367-1
CREATE SEQUENCE IF NOT EXISTS data_source_error_log_seq START WITH 1 INCREMENT BY 50;

-- changeset a_bibik:1737370367-2
CREATE TABLE data_source_error_log
(
    id               BIGINT PRIMARY KEY,
    stacktrace       TEXT          NOT NULL,
    message          VARCHAR(1000) NOT NULL,
    method_signature VARCHAR(500)
);