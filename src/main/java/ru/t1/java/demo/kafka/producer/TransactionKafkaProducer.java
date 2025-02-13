package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.TransactionKafkaDto;

import java.util.UUID;
import java.util.concurrent.CompletionException;

/**
 * Компонент для отправки сообщений Kafka, связанных с сущностью {@link ru.t1.java.demo.model.Transaction}.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionKafkaProducer {
    /**
     * KafkaTemplate для отправки сообщений в Kafka.
     */
    private final KafkaTemplate<String, TransactionKafkaDto> template;

    /**
     * Отправляет сообщение с данными о транзакции в Kafka.
     *
     * @param transactionKafkaDto данные о транзакции для отправки.
     */
    public void send(TransactionKafkaDto transactionKafkaDto) {
        String defaultTopic = template.getDefaultTopic();

        template.send(defaultTopic, UUID.randomUUID().toString(), transactionKafkaDto)
                .thenAccept(result -> log.info("Сообщение о Transaction отправлено успешно: {}", result))
                .exceptionally(ex -> {
                    log.error("Не удалось отправить сообщение о Transaction", ex);
                    throw new CompletionException(ex);
                });
    }
}
