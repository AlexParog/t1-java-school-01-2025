package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.api.TransactionRequestDto;
import ru.t1.java.demo.dto.api.TransactionResponseDto;
import ru.t1.java.demo.dto.kafka.TransactionKafkaDto;

/**
 * Интерфейс сервисного слоя для управления Транзакциями.
 * <p>
 * Предоставляет методы для создания, получения, обновления и архивации транзакций.
 */
public interface TransactionService {
    /**
     * Создает новую транзакцию.
     *
     * @param transactionRequestDto DTO с данными для создания транзакции.
     * @return DTO с данными созданной транзакции.
     */
    TransactionResponseDto createTransaction(TransactionRequestDto transactionRequestDto);

    /**
     * Регистрирует транзакцию на основе данных, полученных из Kafka.
     *
     * @param transactionKafkaDto DTO с данными о транзакции из Kafka.
     */
    void registerTransactionFromKafka(TransactionKafkaDto transactionKafkaDto);

    /**
     * Получает транзакцию по её идентификатору.
     *
     * @param id Идентификатор транзакции.
     * @return DTO с данными транзакции.
     */
    TransactionResponseDto getTransactionById(Long id);

    /**
     * Обновляет транзакцию по её идентификатору.
     *
     * @param id                    Идентификатор транзакции.
     * @param transactionRequestDto DTO с данными для обновления транзакции.
     * @return DTO с данными обновленной транзакции.
     */
    TransactionResponseDto updateTransactionById(Long id, TransactionRequestDto transactionRequestDto);

    /**
     * Архивирует транзакцию по её идентификатору.
     *
     * @param id Идентификатор транзакции.
     * @return DTO с данными архивированной транзакции.
     */
    TransactionResponseDto archiveTransactionById(Long id);
}
