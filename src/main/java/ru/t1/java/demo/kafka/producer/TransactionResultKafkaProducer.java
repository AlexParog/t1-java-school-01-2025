package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.TransactionResultDto;

import java.util.UUID;
import java.util.concurrent.CompletionException;

/**
 * Компонент для отправки сообщений Kafka, связанных с сущностью {@link TransactionResultDto}.
 * (должен лежать в отдельном модуле).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionResultKafkaProducer {

    /**
     * KafkaTemplate для отправки сообщений в Kafka.
     */
    private final KafkaTemplate<String, TransactionResultDto> template;

    /**
     * Отправляет сообщение с данными о результатах транзакций в Kafka.
     *
     * @param transactionResultDto данные о результатах транзакций для отправки.
     */
    public void send(TransactionResultDto transactionResultDto) {
        String defaultTopic = template.getDefaultTopic();

        template.send(defaultTopic, UUID.randomUUID().toString(), transactionResultDto)
                .thenAccept(result -> log.info("Сообщение о TransactionResult отправлено успешно: {}", result))
                .exceptionally(ex -> {
                    log.error("Не удалось отправить сообщение о TransactionResult", ex);
                    throw new CompletionException(ex);
                });
    }
}
