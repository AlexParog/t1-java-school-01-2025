package ru.t1.java.demo.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.MetricMessageDto;

/**
 * Компонент для отправки метрик в Kafka.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class MetricKafkaProducer {
    /**
     * KafkaTemplate для отправки сообщений в Kafka.
     */
    private final KafkaTemplate<String, MetricMessageDto> template;

    /**
     * Отправляет сообщение с метрикой в Kafka.
     *
     * @param metricMessageDto данные о метрике для отправки.
     */
    public void send(MetricMessageDto metricMessageDto) {
        template.sendDefault(metricMessageDto);
    }
}
