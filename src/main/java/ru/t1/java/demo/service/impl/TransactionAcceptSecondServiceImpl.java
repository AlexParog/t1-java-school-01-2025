package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.dto.kafka.TransactionAcceptDto;
import ru.t1.java.demo.dto.kafka.TransactionKey;
import ru.t1.java.demo.dto.kafka.TransactionResultDto;
import ru.t1.java.demo.kafka.producer.TransactionResultKafkaProducer;
import ru.t1.java.demo.model.enums.TransactionStatusEnum;
import ru.t1.java.demo.service.TransactionAcceptSecondService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис для обработки сообщений, полученных из топика t1_demo_transaction_accept.
 * Производит проверку частоты транзакций (с использованием in‑memory кеша) и проверки баланса.
 * (impl второго сервиса, который должен лежать в отдельном модуле).
 */
@Service
@RequiredArgsConstructor
public class TransactionAcceptSecondServiceImpl implements TransactionAcceptSecondService {

    /**
     * Компонент для отправки результатов транзакций в Kafka {@link TransactionResultDto}.
     */
    private final TransactionResultKafkaProducer transactionResultKafkaProducer;

    /**
     * In‑memory кеш для агрегации транзакций по ключу (clientId, accountId).
     * Значением является синхронизированный список временных меток транзакций.
     */
    private final ConcurrentHashMap<TransactionKey, List<LocalDateTime>> transactionCache = new ConcurrentHashMap<>();

    /**
     * Максимальное количество транзакций для одного клиента/счёта в пределах заданного периода T.
     */
    @Value("${transaction.aggregation.max-transactions}")
    private int maxTransactions;

    /**
     * Период времени (в миллисекундах), в течение которого агрегируются транзакции.
     */
    @Value("${transaction.aggregation.period-ms}")
    private long periodMs;

    /**
     * Проверяем условия для подтверждения транзакции и выставляем статус: ACCEPT, BLOCKED, REJECT.
     *
     * @param acceptDto dto для подтверждения транзакции
     */
    @LogDataSourceError
    @Transactional
    @Override
    public void processAcceptTransaction(TransactionAcceptDto acceptDto) {

        TransactionResultDto transactionResultDto = processTransactionLogic(acceptDto);

        transactionResultKafkaProducer.send(transactionResultDto);

    }

    /**
     * Обрабатывает логику проверки транзакции и определяет её статус.
     * Проверяет два основных условия:
     * 1. Превышение частоты транзакций для данного клиента/счёта.
     * 2. Достаточность баланса на счёте для выполнения транзакции.
     * В зависимости от результатов проверки возвращает DTO с соответствующим статусом транзакции:
     * - {@link TransactionStatusEnum#BLOCKED}, если превышена частота транзакций.
     * - {@link TransactionStatusEnum#REJECTED}, если недостаточно средств на счёте.
     * - {@link TransactionStatusEnum#ACCEPTED}, если все проверки пройдены успешно.
     *
     * @param acceptDto DTO с данными о транзакции.
     * @return {@link TransactionResultDto} с результатом обработки транзакции.
     */
    private TransactionResultDto processTransactionLogic(TransactionAcceptDto acceptDto) {

        // Проверка частоты транзакций
        if (isTransactionRateExceeded(acceptDto)) {
            return new TransactionResultDto(
                    acceptDto.getTransactionId(),
                    acceptDto.getAccountId(),
                    TransactionStatusEnum.BLOCKED
            );
        }

        // Проверка баланса счета
        if (isBalanceInsufficient(acceptDto)) {
            return new TransactionResultDto(
                    acceptDto.getTransactionId(),
                    acceptDto.getAccountId(),
                    TransactionStatusEnum.REJECTED
            );
        }

        // Если всё ок, статус ACCEPTED
        return new TransactionResultDto(
                acceptDto.getTransactionId(),
                acceptDto.getAccountId(),
                TransactionStatusEnum.ACCEPTED
        );
    }

    /**
     * Проверяет, превысило ли количество транзакций по данному клиенту/счету порог за период времени.
     *
     * @param transactionAcceptDto данные полученной транзакции
     * @return true если количество транзакций превышает допустимый максимум, иначе false
     */
    private boolean isTransactionRateExceeded(TransactionAcceptDto transactionAcceptDto) {
        //TODO: посмотреть реализации с Redis
        TransactionKey key = new TransactionKey(transactionAcceptDto.getClientId(), transactionAcceptDto.getAccountId());

        // инициализация списка транзакций для ключа, если его нет
        List<LocalDateTime> timestampList = transactionCache.computeIfAbsent(key,
                k -> Collections.synchronizedList(new ArrayList<>()));

        LocalDateTime transactionTime = transactionAcceptDto.getTimestamp();
        LocalDateTime cutoff = transactionTime.minusNanos(periodMs * 1_000_000); // перевод в наносекунды

        synchronized (timestampList) {
            // Удаляем устаревшие записи, вышедшие за рамки периода T
            timestampList.removeIf(ts -> ts.isBefore(cutoff));
            // Добавляем новую транзакцию
            timestampList.add(transactionTime);

            if (timestampList.size() > maxTransactions) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет, достаточно ли баланса для проведения транзакции.
     *
     * @param transactionAcceptDto данные полученной транзакции
     * @return true если сумма транзакции больше баланса, иначе false
     */
    private boolean isBalanceInsufficient(TransactionAcceptDto transactionAcceptDto) {
        BigDecimal amount = transactionAcceptDto.getAmount();
        BigDecimal balance = transactionAcceptDto.getBalance();
        return amount.compareTo(balance) > 0;
    }
}
