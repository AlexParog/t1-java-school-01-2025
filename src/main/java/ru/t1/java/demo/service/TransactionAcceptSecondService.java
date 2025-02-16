package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.kafka.TransactionAcceptDto;

/**
 * Интерфейс сервисного слоя для подтверждения Транзакций (второй сервис, который должен лежать в отдельном модуле).
 */
public interface TransactionAcceptSecondService {
    /**
     * Проверяем условия для подтверждения транзакции и выставляем статус: ACCEPT, BLOCKED, REJECT.
     *
     * @param acceptDto dto для подтверждения транзакции
     */
    void processAcceptTransaction(TransactionAcceptDto acceptDto);
}
