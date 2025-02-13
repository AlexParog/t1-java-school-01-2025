package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.api.TransactionRequestDto;
import ru.t1.java.demo.dto.api.TransactionResponseDto;
import ru.t1.java.demo.dto.kafka.TransactionKafkaDto;
import ru.t1.java.demo.exception.NotFoundException;
import ru.t1.java.demo.kafka.producer.TransactionKafkaProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import ru.t1.java.demo.service.TransactionService;
import ru.t1.java.demo.util.TransactionMapper;

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
     * Компонент для отправки сообщений Kafka, связанных с сущностью {@link Transaction}.
     */
    private final TransactionKafkaProducer transactionKafkaProducer;

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

        // Создание новой транзакции на основе данных из Kafka
        Transaction transaction = new Transaction();
        transaction.setAccount(accountRepository.findById(transactionKafkaDto.getAccountId())
                .orElseThrow(() -> new NotFoundException("Счёт с ID={0} не найден", transactionKafkaDto.getAccountId())));
        transaction.setAmount(transactionKafkaDto.getAmount());
        transaction.setTimeOfPurchase(transactionKafkaDto.getTimeOfPurchase());
        transaction.setArchiveDate(transactionKafkaDto.getArchiveDate());

        transactionRepository.save(transaction);

        log.info("Транзакция успешно обработана: {}", transaction);
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
        Account account = getCurrentAccount(transactionRequestDto);

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
    private Account getCurrentAccount(TransactionRequestDto transactionRequestDto) {
        Long accountId = transactionRequestDto.getAccountId();

        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Счет с ID: {} не найден", accountId);
                    return new NotFoundException("Счет c id={0} не найден", accountId);
                });
    }
}
