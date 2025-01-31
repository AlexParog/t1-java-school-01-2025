package ru.t1.java.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Общая конфигурация Kafka, включающая обработку ошибок при работе с Kafka.
 */
@Slf4j
@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnProperty(value = "t1.kafka.enabled", havingValue = "true")
public class CommonKafkaConfiguration {
    /**
     * Создаёт обработчик ошибок Kafka, который выполняет ограниченное число повторных попыток
     * при возникновении ошибок обработки сообщений.
     *
     * @return экземпляр {@link CommonErrorHandler}.
     */
    @Bean
    public CommonErrorHandler errorHandler() {
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(new FixedBackOff(1000, 3));
        defaultErrorHandler.addNotRetryableExceptions(DeserializationException.class, SerializationException.class);
        defaultErrorHandler.setRetryListeners(((record, ex, deliveryAttempt) -> {
            log.error(" RetryListeners message = {}, offset = {} deliveryAttempt = {}", ex.getMessage(), record.offset(),
                    deliveryAttempt);
        }));
        return defaultErrorHandler;
    }
}
