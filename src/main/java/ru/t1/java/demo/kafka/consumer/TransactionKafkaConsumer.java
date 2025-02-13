package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.TransactionKafkaDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

/**
 * Компонент для обработки сообщений Kafka, связанных с сущностью {@link ru.t1.java.demo.model.Transaction}.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionKafkaConsumer {
    /**
     * Сервис для работы с транзакциями.
     */
    private final TransactionService transactionService;

    /**
     * Обрабатывает сообщения, полученные из Kafka.
     *
     * @param messageTransactionKafkaDtoList список сообщений, содержащих данные о транзакциях.
     * @param ack                            объект для подтверждения обработки сообщений.
     * @param topic                          название топика, из которого получены сообщения.
     * @param key                            ключ сообщения.
     */
    @KafkaListener(id = "transactionListener",
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.account-registration}",
            containerFactory = "transactionKafkaListenerFactory")
    public void listener(@Payload List<TransactionKafkaDto> messageTransactionKafkaDtoList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("Transaction consumer: получены сообщения из topic: {}, key: {}", topic, key);

        try {
            for (TransactionKafkaDto dto : messageTransactionKafkaDtoList) {
                try {
                    transactionService.registerTransactionFromKafka(dto);
                } catch (Exception e) {
                    log.error("Ошибка обработки TransactionKafkaDto {}: {}", dto, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке batch: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }

        log.debug("Transaction consumer: записи обработаны");
    }
}
