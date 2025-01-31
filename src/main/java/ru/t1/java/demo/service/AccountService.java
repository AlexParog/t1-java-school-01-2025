package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.api.AccountCreateRequestDto;
import ru.t1.java.demo.dto.api.AccountResponseDto;
import ru.t1.java.demo.dto.api.AccountUpdateRequestDto;
import ru.t1.java.demo.dto.kafka.AccountKafkaDto;

/**
 * Интерфейс сервисного слоя для управления Счетами.
 * <p>
 * Предоставляет методы для создания, получения, обновления и архивации счетов.
 */
public interface AccountService {
    /**
     * Создает новый счет.
     *
     * @param accountCreateRequestDto DTO с данными для создания счета.
     * @return DTO с данными созданного счета.
     */
    AccountResponseDto createAccount(AccountCreateRequestDto accountCreateRequestDto);

    /**
     * Регистрирует новый счет на основе данных, полученных из Kafka.
     *
     * @param accountKafkaDto dto счета из Kafka.
     */
    void registerAccountFromKafka(AccountKafkaDto accountKafkaDto);

    /**
     * Получает счет по его идентификатору.
     *
     * @param id Идентификатор счета.
     * @return DTO с данными счета.
     */
    AccountResponseDto getAccountById(Long id);

    /**
     * Обновляет счет по его идентификатору.
     *
     * @param id                      Идентификатор счета.
     * @param accountUpdateRequestDto DTO с данными для обновления счета.
     * @return DTO с данными обновленного счета.
     */
    AccountResponseDto updateAccountById(Long id, AccountUpdateRequestDto accountUpdateRequestDto);

    /**
     * Архивирует счет по его идентификатору.
     *
     * @param id Идентификатор счета.
     * @return DTO с данными архивированного счета.
     */
    AccountResponseDto archiveAccountById(Long id);
}
