package ru.t1.java.demo.dto.kafka;

import java.util.UUID;

/**
 * Вспомогательный ключ для агрегации транзакций, основанный на уникальных идентификаторах клиента и счета.
 * Используется в {@link ru.t1.java.demo.service.impl.TransactionAcceptSecondServiceImpl}
 */
public record TransactionKey(UUID clientId, UUID accountId) {
}
