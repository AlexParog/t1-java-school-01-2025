package ru.t1.java.demo.service;

import ru.t1.java.demo.dto.AccountRequestDto;
import ru.t1.java.demo.dto.AccountResponseDto;

/**
 * Интерфейс сервисного слоя для управления Счетами.
 * <p>
 * Предоставляет методы для создания, получения, обновления и архивации счетов.
 */
public interface AccountService {
    /**
     * Создает новый счет.
     *
     * @param accountRequestDto DTO с данными для создания счета.
     * @return DTO с данными созданного счета.
     */
    AccountResponseDto createAccount(AccountRequestDto accountRequestDto);

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
     * @param id                Идентификатор счета.
     * @param accountRequestDto DTO с данными для обновления счета.
     * @return DTO с данными обновленного счета.
     */
    AccountResponseDto updateAccountById(Long id, AccountRequestDto accountRequestDto);

    /**
     * Архивирует счет по его идентификатору.
     *
     * @param id Идентификатор счета.
     * @return DTO с данными архивированного счета.
     */
    AccountResponseDto archiveAccountById(Long id);
}
