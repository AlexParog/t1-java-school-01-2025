package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.TransactionAcceptDto;
import ru.t1.java.demo.service.TransactionAcceptSecondService;

import java.util.List;

/**
 * Компонент для обработки сообщений Kafka, связанных с сущностью {@link TransactionAcceptDto}.
 * (должен лежать в отдельном модуле).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionAcceptKafkaConsumer {
    /**
     * Сервис для подтверждения/определения статуса транзакции.
     */
    private final TransactionAcceptSecondService transactionAcceptSecondService;

    /**
     * Обрабатывает сообщения, полученные из Kafka.
     *
     * @param messageTransactionAcceptDtoList список сообщений, содержащих данные о подтвержденных транзакциях.
     * @param ack                             объект для подтверждения обработки сообщений.
     * @param topic                           название топика, из которого получены сообщения.
     * @param key                             ключ сообщения.
     */
    @KafkaListener(id = "transactionAcceptListener",
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.transaction-accept-status}",
            containerFactory = "transactionAcceptKafkaListenerFactory")
    public void listener(@Payload List<TransactionAcceptDto> messageTransactionAcceptDtoList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("TransactionAccept consumer: получены сообщения из topic: {}, key: {}", topic, key);

        try {
            for (TransactionAcceptDto dto : messageTransactionAcceptDtoList) {
                try {
                    transactionAcceptSecondService.processAcceptTransaction(dto);
                } catch (Exception e) {
                    log.error("Ошибка обработки TransactionAcceptDto {}: {}", dto, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке batch TransactionAcceptDto: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }

        log.debug("TransactionAccept consumer: записи обработаны");

    }
}
