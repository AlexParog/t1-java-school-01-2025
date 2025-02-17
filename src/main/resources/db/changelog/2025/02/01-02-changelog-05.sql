-- liquibase formatted sql

-- changeset a_bibik:1739109944-1
-- Обновление таблицы transaction: добавление status
UPDATE transaction
SET status = 'ACCEPTED'
WHERE transaction.id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

-- changeset a_bibik:1739109944-2
-- Обновление таблицы account: добавление status
UPDATE account
SET status = 'OPEN'
WHERE account.id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);