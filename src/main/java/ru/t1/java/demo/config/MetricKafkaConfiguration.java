package ru.t1.java.demo.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.t1.java.demo.dto.kafka.MetricMessageDto;
import ru.t1.java.demo.kafka.producer.MetricKafkaProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация продюсера Kafka для отправки метрик выполнения методов.
 * <p>
 * Включается только при наличии свойства {@code t1.kafka.enabled=true}.
 */
@Configuration
@ConditionalOnProperty(value = "t1.kafka.enabled", havingValue = "true")
public class MetricKafkaConfiguration {

    /**
     * Конфиги.
     */
    private final KafkaProperties kafkaProperties;

    /**
     * Конструктор с внедрением настроек Kafka.
     *
     * @param kafkaProperties конфигурационные свойства Kafka.
     */
    public MetricKafkaConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Фабрика продюсеров Kafka для отправки сообщений с метриками.
     *
     * @return экземпляр {@link ProducerFactory}.
     */
    @Bean
    public ProducerFactory<String, MetricMessageDto> metricProducerFactory() {
        return createProducerFactory();
    }

    /**
     * Шаблон Kafka для отправки сообщений с метриками.
     *
     * @param producerFactory фабрика продюсеров Kafka.
     * @return экземпляр {@link KafkaTemplate}.
     */
    @Bean
    public KafkaTemplate<String, MetricMessageDto> kafkaMetricTemplate(
            @Qualifier("metricProducerFactory") ProducerFactory<String, MetricMessageDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Создаёт и настраивает продюсер Kafka для отправки метрик.
     * <p>
     * Включается только при наличии свойства {@code t1.kafka.producer.enable=true} (по умолчанию включён).
     *
     * @param template шаблон Kafka для отправки сообщений.
     * @return экземпляр {@link MetricKafkaProducer}.
     */
    @Bean
    @ConditionalOnProperty(value = "${t1.kafka.producer.enable}",
            havingValue = "true",
            matchIfMissing = true)
    public MetricKafkaProducer producerMatric(
            @Qualifier("kafkaMetricTemplate") KafkaTemplate<String, MetricMessageDto> template) {
        template.setDefaultTopic(kafkaProperties.getTopic().getMethodsMetric());
        return new MetricKafkaProducer(template);
    }

    /**
     * Создаёт фабрику продюсеров Kafka с настройками.
     *
     * @return экземпляр {@link ProducerFactory}.
     */
    private ProducerFactory<String, MetricMessageDto> createProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }
}