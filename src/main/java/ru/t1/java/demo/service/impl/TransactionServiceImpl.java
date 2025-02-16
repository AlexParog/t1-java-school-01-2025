package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.api.TransactionRequestDto;
import ru.t1.java.demo.dto.api.TransactionResponseDto;
import ru.t1.java.demo.dto.kafka.TransactionAcceptDto;
import ru.t1.java.demo.dto.kafka.TransactionKafkaDto;
import ru.t1.java.demo.dto.kafka.TransactionResultDto;
import ru.t1.java.demo.exception.AccountNotOpenException;
import ru.t1.java.demo.exception.InsufficientFundsException;
import ru.t1.java.demo.exception.NotFoundException;
import ru.t1.java.demo.exception.UnknownTransactionStatusException;
import ru.t1.java.demo.kafka.producer.TransactionAcceptKafkaProducer;
import ru.t1.java.demo.kafka.producer.TransactionKafkaProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatusEnum;
import ru.t1.java.demo.model.enums.TransactionStatusEnum;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Реализация сервисного слоя для управления сущностью {@link Transaction}.
 * Предоставляет методы для создания, обновления, архивирования и получения транзакций.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    /**
     * Репозиторий для работы с сущностью {@link Transaction}.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Репозиторий для работы с сущностью {@link Account}.
     */
    private final AccountRepository accountRepository;

    /**
     * Маппер для преобразования DTO в сущности и обратно.
     */
    private final TransactionMapper transactionMapper;

    /**
     * Компонент для отправки входящий транзакций в Kafka {@link TransactionKafkaDto}.
     */
    private final TransactionKafkaProducer transactionKafkaProducer;

    /**
     * Компонент для отправки подтвержденных транзакций в Kafka {@link TransactionAcceptDto}.
     */
    private final TransactionAcceptKafkaProducer transactionAcceptKafkaProducer;

    /**
     * Создаёт новую транзакцию на основе данных из DTO.
     *
     * @param transactionRequestDto DTO с данными для создания транзакции.
     * @return DTO с данными созданной транзакции.
     */
    @Transactional
    @LogDataSourceError
    @Metric(10)
    @Override
    public TransactionResponseDto createTransaction(TransactionRequestDto transactionRequestDto) {
        log.info("Создание транзакции: {}", transactionRequestDto);

        // Сохранение транзакции в БД
        Transaction transaction = saveTransactionToDatabase(transactionRequestDto);

        // Отправка в Kafka
        sendTransactionToKafka(transaction);

        TransactionResponseDto transactionResponseDto = transactionMapper.toTransactionResponseDto(transaction);
        log.info("Транзакция успешно создана с ID: {}", transactionResponseDto.getId());
        return transactionResponseDto;
    }

    /**
     * Регистрирует транзакцию на основе данных, полученных из Kafka.
     *
     * @param transactionKafkaDto DTO с данными о транзакции из Kafka.
     */
    @Transactional
    @LogDataSourceError
    @Metric(10)
    @Override
    public void registerTransactionFromKafka(TransactionKafkaDto transactionKafkaDto) {
        log.info("Обработка транзакции из Kafka: {}", transactionKafkaDto);

        Account account = validateAndFetchAccount(transactionKafkaDto);

        // Создание новой транзакции на основе данных из Kafka
        Transaction transaction = createAndSaveTransaction(transactionKafkaDto, account);

        log.info("Сохранена транзакция с ID: {} и статусом: {}", transaction.getId(), transaction.getStatus());

        // Изменяем баланс счета (временно уменьшаем на сумму транзакции)
        BigDecimal newBalance = account.getBalance().subtract(transactionKafkaDto.getAmount());
        accountRepository.updateBalanceByAccountId(account.getAccountId(), newBalance);

        log.info("Обновлен баланс счета с ID {}: новый баланс {}", account.getId(), newBalance);

        // отправка
        sendTransactionAccept(transaction, account);

        log.info("Транзакция успешно обработана: {}", transaction);
    }

    /**
     * Обрабатываем информацию о результате транзакции.
     *
     * @param transactionResultDto информация о результате транзакции
     */
    @Transactional
    @LogDataSourceError
    @Override
    public void handleTransactionResult(TransactionResultDto transactionResultDto) {
        log.info("Обработка результата транзакции из Kafka: {}", transactionResultDto);

        Account curAccount = accountRepository.findAccountByAccountId(transactionResultDto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Счет с уникальным ID={0} не найден",
                        transactionResultDto.getAccountId()));

        Transaction curTransaction = transactionRepository.findTransactionByTransactionId(transactionResultDto
                .getTransactionId()).orElseThrow(() -> new NotFoundException("Транзакция с уникальным ID={0} не найдена"));

        TransactionStatusEnum resultStatus = transactionResultDto.getStatus();
        switch (resultStatus) {
            case ACCEPTED -> {
                log.info("Транзакция {} принята. Обновление статуса на ACCEPTED.", transactionResultDto.getTransactionId());
                transactionRepository.updateStatusByTransactionId(
                        transactionResultDto.getTransactionId(),
                        TransactionStatusEnum.ACCEPTED);
            }
            case BLOCKED -> {
                log.info("Транзакция {} заблокирована. Обновление статуса на BLOCKED.", transactionResultDto.getTransactionId());
                transactionRepository.updateStatusByTransactionId(
                        transactionResultDto.getTransactionId(),
                        TransactionStatusEnum.BLOCKED);

                accountRepository.updateStatusByAccountId(transactionResultDto.getAccountId(), AccountStatusEnum.BLOCKED);

                // Корректируем баланс и frozenAmount
                BigDecimal newBalance = curAccount.getBalance().subtract(curTransaction.getAmount());
                BigDecimal newFrozenAmount = curAccount.getFrozenAmount().add(curTransaction.getAmount());

                accountRepository.updateBalanceByAccountId(curAccount.getAccountId(), newBalance);
                accountRepository.updateFrozenAmountByAccountId(curAccount.getAccountId(), newFrozenAmount);
            }
            case REJECTED -> {
                log.info("Транзакция {} отменена. Обновление статуса на REJECTED.", transactionResultDto.getTransactionId());
                transactionRepository.updateStatusByTransactionId(
                        transactionResultDto.getTransactionId(),
                        TransactionStatusEnum.REJECTED);

                // Возвращаем сумму транзакции на баланс счета
                BigDecimal preTransactionBalance = curAccount.getBalance().add(curTransaction.getAmount());
                accountRepository.updateBalanceByAccountId(curAccount.getAccountId(), preTransactionBalance);
            }
            default -> {
                log.error("Неизвестный статус транзакции: {}", resultStatus);
                throw new UnknownTransactionStatusException("Неизвестный статус транзакции: {0}", resultStatus);
            }
        }
    }

    /**
     * Получает транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции.
     * @return DTO с данными найденной транзакции.
     */
    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public TransactionResponseDto getTransactionById(Long id) {
        log.info("Получение транзакции с ID: {}", id);

        Transaction transaction = findTransactionOrNotFound(id);
        log.debug("Транзакция найдена: {}", transaction);

        return transactionMapper.toTransactionResponseDto(transaction);
    }

    /**
     * Обновляет данные транзакции по её идентификатору.
     *
     * @param id                    идентификатор транзакции.
     * @param transactionRequestDto DTO с обновлёнными данными транзакции.
     * @return DTO с данными обновлённой транзакции.
     */
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

    /**
     * Архивирует транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции.
     * @return DTO с данными архивированной транзакции.
     */
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

    /**
     * Находит транзакцию по её идентификатору или выбрасывает исключение, если транзакция не найдена.
     *
     * @param id идентификатор транзакции.
     * @return найденная транзакция.
     * @throws NotFoundException если транзакция не найдена.
     */
    private Transaction findTransactionOrNotFound(Long id) {
        log.debug("Поиск транзакции с ID: {}", id);

        return transactionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Транзакция с ID: {} не найдена", id);
                    return new NotFoundException("Транзакция c id={0} не найдена", id);
                });
    }

    /**
     * Сохраняет транзакцию в базу данных.
     *
     * @param transactionRequestDto DTO с данными для создания транзакции.
     * @return сохранённая транзакция.
     */
    private Transaction saveTransactionToDatabase(TransactionRequestDto transactionRequestDto) {
        Account account = findAccountForTransaction(transactionRequestDto);

        Transaction transaction = transactionMapper.toTransaction(transactionRequestDto);
        transaction.setAccount(account);
        transactionRepository.save(transaction);

        return transaction;
    }

    /**
     * Отправляет данные о транзакции в Kafka.
     *
     * @param transaction транзакция, данные которой нужно отправить.
     */
    private void sendTransactionToKafka(Transaction transaction) {
        TransactionKafkaDto transactionKafkaDto = new TransactionKafkaDto();
        transactionKafkaDto.setAccountId(transaction.getAccount().getId());
        transactionKafkaDto.setAmount(transaction.getAmount());
        transactionKafkaDto.setTimeOfPurchase(transaction.getTimeOfPurchase());
        transactionKafkaDto.setArchiveDate(transaction.getArchiveDate());

        transactionKafkaProducer.send(transactionKafkaDto);
    }

    /**
     * Находит счёт по его идентификатору или выбрасывает исключение, если счёт не найден.
     *
     * @param transactionRequestDto DTO с данными о транзакции.
     * @return найденный счёт.
     * @throws NotFoundException если счёт не найден.
     */
    private Account findAccountForTransaction(TransactionRequestDto transactionRequestDto) {
        Long accountId = transactionRequestDto.getAccountId();

        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Счет с ID: {} не найден", accountId);
                    return new NotFoundException("Счет c id={0} не найден", accountId);
                });
    }

    /**
     * Проверяет и возвращает счет на основе данных из DTO транзакции.
     * Выполняет следующие проверки:
     * - Существование счета по ID.
     * - Открыт ли счет для транзакций (статус счета должен быть OPEN).
     * - Достаточно ли средств на счете для выполнения транзакции.
     *
     * @param transactionKafkaDto DTO с данными о транзакции из Kafka.
     * @return Найденный и проверенный счет.
     */
    private Account validateAndFetchAccount(TransactionKafkaDto transactionKafkaDto) {
        Account account = accountRepository.findById(transactionKafkaDto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Счёт с ID={0} не найден", transactionKafkaDto.getAccountId()));

        if (!AccountStatusEnum.OPEN.equals(account.getStatus())) {
            log.warn("Счет с ID={} не открыт для транзакций. Текущий статус: {}", account.getId(), account.getStatus());
            throw new AccountNotOpenException("Счет с ID={0} не открыт, текущий статус: {1}",
                    account.getId(), account.getStatus());
        }

        if (account.getBalance().compareTo(transactionKafkaDto.getAmount()) < 0) {
            log.warn("Недостаточно средств на счете с ID={}. Текущий баланс: {}", account.getId(), account.getBalance());
            throw new InsufficientFundsException("На счете с ID={0} недостаточно средств, текущий баланс={1}",
                    account.getId(), account.getBalance());
        }
        return account;
    }

    /**
     * Создает и сохраняет транзакцию на основе данных из DTO и связанного счета.
     * Устанавливает статус транзакции как REQUESTED.
     *
     * @param transactionKafkaDto DTO с данными о транзакции из Kafka.
     * @param account             Счет, связанный с транзакцией.
     * @return Созданная и сохраненная транзакция.
     */
    private Transaction createAndSaveTransaction(TransactionKafkaDto transactionKafkaDto, Account account) {
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(transactionKafkaDto.getAmount());
        transaction.setTimeOfPurchase(transactionKafkaDto.getTimeOfPurchase());
        transaction.setStatus(TransactionStatusEnum.REQUESTED);
        transaction.setArchiveDate(transactionKafkaDto.getArchiveDate());
        transactionRepository.save(transaction);
        return transaction;
    }

    /**
     * Отправляет подтверждение транзакции в Kafka.
     * Формирует DTO с данными о транзакции и счете, затем отправляет его через Kafka Producer.
     *
     * @param transaction Транзакция, для которой отправляется подтверждение.
     * @param account     Счет, связанный с транзакцией.
     */
    private void sendTransactionAccept(Transaction transaction, Account account) {
        TransactionAcceptDto acceptDto = new TransactionAcceptDto();
        acceptDto.setTransactionId(transaction.getTransactionId());
        acceptDto.setAccountId(account.getAccountId());
        acceptDto.setClientId(account.getClient().getClientId());
        acceptDto.setTimestamp(transaction.getTimeOfPurchase());
        acceptDto.setAmount(transaction.getAmount());
        acceptDto.setBalance(account.getBalance());

        transactionAcceptKafkaProducer.send(acceptDto);
    }
}