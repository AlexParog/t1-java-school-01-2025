package ru.t1.java.demo.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Конфигурационные свойства Kafka, загружаемые из файла конфигурации (например, application.yml).
 * Используется для настройки соединения с Kafka, потребителей, продюсеров и параметров обработки сообщений.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "t1.kafka")
public class KafkaProperties {
    /**
     * Адрес сервера Kafka (bootstrap servers).
     */
    private String bootstrapServer;

    /**
     * Флаг включения/отключения Kafka.
     */
    private boolean enabled;

    /**
     * Настройки потребителей Kafka.
     */
    private ConsumerProperties consumer;

    /**
     * Настройки продюсеров Kafka.
     */
    private ProducerProperties producer;

    /**
     * Настройки топиков Kafka.
     */
    private TopicProperties topic;

    /**
     * Настройки слушателей Kafka.
     */
    private ListenerProperties listener;

    /**
     * Максимальные параметры Kafka.
     */
    private MaxProperties max;

    /**
     * Настройки потребителя Kafka.
     */
    @Getter
    @Setter
    public static class ConsumerProperties {
        /**
         * Идентификатор группы потребителей.
         */
        private String groupId;

        /**
         * Максимальное количество записей для обработки за один вызов poll().
         */
        private String maxPollRecords;

        /**
         * Тайм-аут сессии потребителя Kafka.
         */
        private String sessionTimeout;

        /**
         * Интервал отправки heartbeat-сообщений потребителя.
         */
        private String heartbeatInterval;
    }

    /**
     * Настройки продюсера Kafka.
     */
    @Getter
    @Setter
    public static class ProducerProperties {
        /**
         * Флаг включения продюсера Kafka.
         */
        private boolean enable;
    }

    /**
     * Настройки топиков Kafka.
     */
    @Getter
    @Setter
    public static class TopicProperties {
        /**
         * Топик для регистрации аккаунтов.
         */
        private String accountRegistration;

        /**
         * Топик для регистрации транзакций.
         */
        private String transactionRegistration;

        /**
         * Топик для метрик выполнения методов.
         */
        private String methodsMetric;
    }

    /**
     * Настройки слушателя Kafka.
     */
    @Getter
    @Setter
    public static class ListenerProperties {
        /**
         * Тайм-аут ожидания сообщений при вызове poll().
         */
        private String pollTimeout;
    }

    /**
     * Максимальные параметры Kafka.
     */
    @Getter
    @Setter
    public static class MaxProperties {
        /**
         * Максимальный размер извлекаемых данных из раздела Kafka.
         */
        private String partitionFetchBytes;

        /**
         * Настройки обработки сообщений в Kafka.
         */
        private PollProperties poll;

        /**
         * Настройки опроса Kafka.
         */
        @Getter
        @Setter
        public static class PollProperties {
            /**
             * Максимальное количество записей за один poll().
             */
            private String records;

            /**
             * Интервал между вызовами poll() в миллисекундах.
             */
            private String intervalMs;
        }
    }
}