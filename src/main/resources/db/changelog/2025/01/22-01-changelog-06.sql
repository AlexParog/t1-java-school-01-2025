-- liquibase formatted sql

-- changeset a_bibik:1737497664-1
INSERT INTO account (id, client_id, account_type, balance)
VALUES (1, 1, 'DEBIT', 1500.00),
       (2, 1, 'CREDIT', 500.50),
       (3, 2, 'DEBIT', 2500.75),
       (4, 3, 'DEBIT', 750.20),
       (5, 4, 'CREDIT', 1200.90),
       (6, 5, 'DEBIT', 3000.00),
       (7, 6, 'CREDIT', 800.40),
       (8, 7, 'DEBIT', 1800.60),
       (9, 8, 'CREDIT', 2200.10),
       (10, 9, 'DEBIT', 950.30);