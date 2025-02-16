package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.TransactionAcceptDto;

import java.util.UUID;
import java.util.concurrent.CompletionException;

/**
 * Компонент для отправки сообщений Kafka, связанных с сущностью {@link TransactionAcceptDto}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionAcceptKafkaProducer {
    /**
     * KafkaTemplate для отправки сообщений в Kafka.
     */
    private final KafkaTemplate<String, TransactionAcceptDto> template;

    /**
     * Отправляет сообщение с данными о подтвержденной транзакции в Kafka.
     *
     * @param transactionAcceptDto данные о подтвержденной транзакции для отправки.
     */
    public void send(TransactionAcceptDto transactionAcceptDto) {
        String defaultTopic = template.getDefaultTopic();

        template.send(defaultTopic, UUID.randomUUID().toString(), transactionAcceptDto)
                .thenAccept(result -> log.info("Сообщение о TransactionAccept отправлено успешно: {}", result))
                .exceptionally(ex -> {
                    log.error("Не удалось отправить сообщение о TransactionAccept", ex);
                    throw new CompletionException(ex);
                });
    }
}
