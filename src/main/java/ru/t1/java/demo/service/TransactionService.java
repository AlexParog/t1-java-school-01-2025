package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.api.TransactionRequestDto;
import ru.t1.java.demo.dto.api.TransactionResponseDto;
import ru.t1.java.demo.dto.kafka.TransactionKafkaDto;
import ru.t1.java.demo.dto.kafka.TransactionResultDto;

/**
 * Интерфейс сервисного слоя для управления Транзакциями.
 * <p>
 * Предоставляет методы для создания, получения, обновления и архивации транзакций.
 */
public interface TransactionService {
    /**
     * Создает новую входящую транзакцию.
     *
     * @param transactionRequestDto DTO с данными для создания входящей транзакции.
     * @return DTO с данными созданной входящей транзакции.
     */
    TransactionResponseDto createTransaction(TransactionRequestDto transactionRequestDto);

    /**
     * Регистрирует входящую транзакцию на основе данных, полученных из Kafka.
     *
     * @param transactionKafkaDto DTO с данными о входящей транзакции из Kafka.
     */
    void registerTransactionFromKafka(TransactionKafkaDto transactionKafkaDto);

    /**
     * Обрабатываем информацию о результате транзакции.
     *
     * @param transactionResultDto информация о результате транзакции
     */
    void handleTransactionResult(TransactionResultDto transactionResultDto);

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
