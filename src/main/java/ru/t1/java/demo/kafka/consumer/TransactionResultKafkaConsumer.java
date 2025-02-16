package ru.t1.java.demo.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.TransactionResultDto;
import ru.t1.java.demo.service.TransactionService;

import java.util.List;

/**
 * Компонент для обработки сообщений Kafka, связанных с сущностью {@link TransactionResultDto}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionResultKafkaConsumer {
    /**
     * Сервис для работы со транзакциями.
     */
    private final TransactionService transactionService;

    /**
     * Обрабатывает сообщения, полученные из Kafka.
     *
     * @param messageTransactionResultDtoList список сообщений, содержащих данные о результатах транзакций.
     * @param ack                             объект для подтверждения обработки сообщений.
     * @param topic                           название топика, из которого получены сообщения.
     * @param key                             ключ сообщения.
     */
    @KafkaListener(id = "transactionResultListener",
            groupId = "${t1.kafka.consumer.group-id}",
            topics = "${t1.kafka.topic.transaction-result-status}",
            containerFactory = "transactionResultKafkaListenerFactory"
    )
    public void listener(@Payload List<TransactionResultDto> messageTransactionResultDtoList,
                         Acknowledgment ack,
                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                         @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("TransactionResult consumer: получены сообщения из topic: {}, key: {}", topic, key);

        try {
            for (TransactionResultDto dto : messageTransactionResultDtoList) {
                try {
                    transactionService.handleTransactionResult(dto);
                } catch (Exception e) {
                    log.error("Ошибка обработки TransactionResultDto {}: {}", dto, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при обработке batch TransactionResultDto: {}", e.getMessage(), e);
        } finally {
            ack.acknowledge();
        }

        log.debug("TransactionResult consumer: записи обработаны");

    }
}
