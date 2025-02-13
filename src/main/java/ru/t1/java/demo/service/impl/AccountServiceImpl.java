package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.AccountRequestDto;
import ru.t1.java.demo.dto.AccountResponseDto;
import ru.t1.java.demo.exception.NotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.time.LocalDateTime;

/**
 * Реализация сервисного слоя для управления Счетами.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final ClientRepository clientRepository;


    @Transactional
    @LogDataSourceError
    @Override
    public AccountResponseDto createAccount(AccountRequestDto accountRequestDto) {
        log.info("Создание счета: {}", accountRequestDto);

        Long clientId = accountRequestDto.getClientId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Клиент с ID: {} не найден", clientId);
                    return new NotFoundException("Клиент c id={0} не найден", clientId);
                });

        Account account = accountMapper.toAccount(accountRequestDto);
        account.setClient(client);
        accountRepository.save(account);

        AccountResponseDto accountResponseDto = accountMapper.toAccountResponseDto(account);
        log.info("Счет успешно создан с ID: {}", accountResponseDto.getId());
        return accountResponseDto;
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public AccountResponseDto getAccountById(Long id) {
        log.info("Получение счета с ID: {}", id);

        Account account = findAccountOrNotFound(id);
        log.debug("Счет найден: {}", account);

        return accountMapper.toAccountResponseDto(account);
    }

    @Transactional
    @LogDataSourceError
    @Override
    public AccountResponseDto updateAccountById(Long id, AccountRequestDto accountRequestDto) {
        log.info("Обновление счета с ID: {} данными: {}", id, accountRequestDto);

        Account account = findAccountOrNotFound(id);
        accountMapper.updateAccountFromDto(accountRequestDto, account);

        accountRepository.save(account);
        log.info("Счет с ID: {} успешно обновлен", id);

        return accountMapper.toAccountResponseDto(account);
    }

    @Transactional
    @LogDataSourceError
    @Override
    public AccountResponseDto archiveAccountById(Long id) {
        log.info("Архивирование счета с ID: {}", id);

        Account account = findAccountOrNotFound(id);

        account.setArchiveDate(LocalDateTime.now());
        accountRepository.save(account);

        log.info("Счет с ID: {} успешно архивирован на дату: {}", id, account.getArchiveDate());

        return accountMapper.toAccountResponseDto(account);
    }

    protected Account findAccountOrNotFound(Long id) {
        log.debug("Поиск счета с ID: {}", id);

        return accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Счет с ID: {} не найден", id);
                    return new NotFoundException("Счет c id={0} не найден", id);
                });
    }
}