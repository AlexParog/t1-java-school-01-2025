package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.AccountKafkaDto;

import java.util.UUID;
import java.util.concurrent.CompletionException;

/**
 * Компонент для отправки сообщений Kafka, связанных с сущностью {@link ru.t1.java.demo.model.Account}.
 * Отправляет данные о счетах в указанный топик Kafka.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AccountKafkaProducer {
    /**
     * KafkaTemplate для отправки сообщений в Kafka.
     */
    private final KafkaTemplate<String, AccountKafkaDto> template;

    /**
     * Отправляет сообщение с данными о счёте в Kafka.
     *
     * @param accountKafkaDto данные о счёте для отправки.
     */
    public void send(AccountKafkaDto accountKafkaDto) {
        String defaultTopic = template.getDefaultTopic();
        template.send(defaultTopic, UUID.randomUUID().toString(), accountKafkaDto)
                .thenAccept(result -> log.info("Сообщение Account отправлено успешно: {}", result))
                .exceptionally(ex -> {
                    log.error("Не удалось отправить сообщение о состоянии Account", ex);
                    throw new CompletionException(ex);
                });
    }
}

