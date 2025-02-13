-- liquibase formatted sql

-- changeset a_bibik:1737497679-1
INSERT INTO transaction (id, account_id, amount, time_of_purchase)
VALUES (1, 1, 100.00, CURRENT_TIMESTAMP - INTERVAL '1 day'),
       (2, 1, 50.00, CURRENT_TIMESTAMP - INTERVAL '2 hour'),
       (3, 2, 25.50, CURRENT_TIMESTAMP - INTERVAL '3 day'),
       (4, 2, 120.75, CURRENT_TIMESTAMP - INTERVAL '4 hour'),
       (5, 3, 10.20, CURRENT_TIMESTAMP - INTERVAL '5 day'),
       (6, 3, 300.90, CURRENT_TIMESTAMP - INTERVAL '6 hour'),
       (7, 4, 75.00, CURRENT_TIMESTAMP - INTERVAL '7 day'),
       (8, 4, 500.40, CURRENT_TIMESTAMP - INTERVAL '8 hour'),
       (9, 5, 200.60, CURRENT_TIMESTAMP - INTERVAL '9 day'),
       (10, 5, 90.10, CURRENT_TIMESTAMP - INTERVAL '10 hour');