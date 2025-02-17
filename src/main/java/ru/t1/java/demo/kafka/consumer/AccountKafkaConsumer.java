package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.AccountKafkaDto;
import ru.t1.java.demo.service.AccountService;

import java.util.List;

/**
 * Компонент для обработки сообщений Kafka, связанных с сущностью {@link ru.t1.java.demo.model.Account}.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AccountKafkaConsumer {
    /**
     * Сервис для работы со счетами.
     */
    private final AccountService accountService;

    /**
     * Обрабатывает сообщения, полученные из Kafka.
     *
     * @param messageAccountKafkaDtoList список сообщений, содержащих данные о счетах.
     * @param ack                        объект для подтверждения обработки сообщений.
     * @param topic                      название топика, из которого получены сообщения.
     * @param key                        ключ сообщения.
     */
    @KafkaListener(id = "accountListener",
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.account-registration}",
            containerFactory = "accountKafkaListenerFactory")
    public void listener(@Payload List<AccountKafkaDto> messageAccountKafkaDtoList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Account consumer: получены сообщения из topic: {}, key: {}", topic, key);

        try {
            for (AccountKafkaDto dto : messageAccountKafkaDtoList) {
                try {
                    accountService.registerAccountFromKafka(dto);
                } catch (Exception e) {
                    log.error("Ошибка обработки AccountKafkaDto {}: {}", dto, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке batch: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }

        log.debug("Account consumer: записи обработаны");
    }
}