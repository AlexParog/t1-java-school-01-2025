package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.TransactionRequestDto;
import ru.t1.java.demo.dto.TransactionResponseDto;
import ru.t1.java.demo.exception.NotFoundException;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.time.LocalDateTime;

/**
 * Реализация сервисного слоя для управления Транзакциями.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    @LogDataSourceError
    @Override
    public TransactionResponseDto createTransaction(TransactionRequestDto transactionRequestDto) {
        log.info("Создание транзакции: {}", transactionRequestDto);

        Long accountId = transactionRequestDto.getAccountId();
        Account currentAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Счет с ID: {} не найден", accountId);
                    return new NotFoundException("Счет c id={0} не найден", accountId);
                });

        Transaction transaction = transactionMapper.toTransaction(transactionRequestDto);
        transaction.setAccount(currentAccount);
        transactionRepository.save(transaction);

        TransactionResponseDto transactionResponseDto = transactionMapper.toTransactionResponseDto(transaction);
        log.info("Транзакция успешно создана с ID: {}", transactionResponseDto.getId());
        return transactionResponseDto;
    }

    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public TransactionResponseDto getTransactionById(Long id) {
        log.info("Получение транзакции с ID: {}", id);

        Transaction transaction = findTransactionOrNotFound(id);
        log.debug("Транзакция найдена: {}", transaction);

        return transactionMapper.toTransactionResponseDto(transaction);
    }

    @Transactional
    @LogDataSourceError
    @Override
    public TransactionResponseDto updateTransactionById(Long id, TransactionRequestDto transactionRequestDto) {
        log.info("Обновление транзакции с ID: {} данными: {}", id, transactionRequestDto);

        Transaction transaction = findTransactionOrNotFound(id);
        transactionMapper.updateTransactionFromDto(transactionRequestDto, transaction);

        transactionRepository.save(transaction);
        log.info("Транзакция с ID: {} успешно обновлена", id);

        return transactionMapper.toTransactionResponseDto(transaction);
    }

    @Transactional
    @LogDataSourceError
    @Override
    public TransactionResponseDto archiveTransactionById(Long id) {
        log.info("Архивирование транзакции с ID: {}", id);

        Transaction transaction = findTransactionOrNotFound(id);

        transaction.setArchiveDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        log.info("Транзакция с ID: {} успешно архивирована на дату: {}", id, transaction.getArchiveDate());

        return transactionMapper.toTransactionResponseDto(transaction);
    }

    private Transaction findTransactionOrNotFound(Long id) {
        log.debug("Поиск транзакции с ID: {}", id);

        return transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Транзакция с ID: {} не найдена", id);
                    return new NotFoundException("Транзакция c id={0} не найдена", id);
                });
    }
}
