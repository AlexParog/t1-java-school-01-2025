package ru.t1.java.demo.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.t1.java.demo.dto.kafka.TransactionAcceptDto;
import ru.t1.java.demo.dto.kafka.TransactionKafkaDto;
import ru.t1.java.demo.dto.kafka.TransactionResultDto;
import ru.t1.java.demo.kafka.MessageDeserializer;
import ru.t1.java.demo.kafka.producer.TransactionAcceptKafkaProducer;
import ru.t1.java.demo.kafka.producer.TransactionKafkaProducer;
import ru.t1.java.demo.kafka.producer.TransactionResultKafkaProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka для обработки сообщений о транзакциях.
 * <p>
 * Данный класс определяет фабрики продюсеров и консьюмеров Kafka,
 * а также настраивает шаблон KafkaTemplate для отправки сообщений.
 */
@Configuration
@ConditionalOnProperty(value = "t1.kafka.enabled", havingValue = "true")
public class TransactionKafkaConfiguration {
    /**
     * Конфиг.
     */
    private final KafkaProperties kafkaProperties;

    /**
     * Конструктор для инициализации конфигурации Kafka.
     *
     * @param kafkaProperties свойства Kafka, необходимые для настройки фабрик и шаблонов.
     */
    public TransactionKafkaConfiguration(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Создает фабрику консьюмера Kafka для обработки сообщений с транзакциями.
     *
     * @return {@link ConsumerFactory} для {@link TransactionKafkaDto}.
     */
    @Bean
    public ConsumerFactory<String, TransactionKafkaDto> transactionConsumerFactory() {
        return createConsumerFactory(TransactionKafkaDto.class);
    }

    /**
     * Создает фабрику консьюмера Kafka для обработки сообщений о результатах транзакций.
     *
     * @return {@link ConsumerFactory} для {@link TransactionResultDto}.
     */
    @Bean
    public ConsumerFactory<String, TransactionResultDto> transactionResultConsumerFactory() {
        return createConsumerFactory(TransactionResultDto.class);
    }

    /**
     * Создает фабрику консьюмера Kafka для обработки сообщений о подтвержденных транзакций.
     *
     * @return {@link ConsumerFactory} для {@link TransactionAcceptDto}.
     */
    @Bean
    public ConsumerFactory<String, TransactionAcceptDto> transactionAcceptConsumerFactory() {
        return createConsumerFactory(TransactionAcceptDto.class);
    }

    /**
     * Создает фабрику слушателей Kafka для входящих транзакций {@link TransactionKafkaDto}.
     *
     * @param consumerFactory фабрика консьюмеров.
     * @param errorHandler    обработчик ошибок Kafka.
     * @return фабрика контейнера слушателей Kafka.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionKafkaDto> transactionKafkaListenerFactory(
            @Qualifier("transactionConsumerFactory") ConsumerFactory<String, TransactionKafkaDto> consumerFactory,
            CommonErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionKafkaDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerFactoryBuilder(consumerFactory, factory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    /**
     * Создает фабрику слушателей Kafka для результатов транзакций {@link TransactionResultDto}.
     *
     * @param consumerFactory фабрика консьюмеров.
     * @param errorHandler    обработчик ошибок Kafka.
     * @return фабрика контейнера слушателей Kafka.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionResultDto> transactionResultKafkaListenerFactory(
            @Qualifier("transactionResultConsumerFactory") ConsumerFactory<String, TransactionResultDto> consumerFactory,
            CommonErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionResultDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerFactoryBuilder(consumerFactory, factory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    /**
     * Создает фабрику слушателей Kafka для подтвержденных транзакций {@link TransactionAcceptDto}.
     *
     * @param consumerFactory фабрика консьюмеров.
     * @param errorHandler    обработчик ошибок Kafka.
     * @return фабрика контейнера слушателей Kafka.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionAcceptDto> transactionAcceptKafkaListenerFactory(
            @Qualifier("transactionAcceptConsumerFactory") ConsumerFactory<String, TransactionAcceptDto> consumerFactory,
            CommonErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, TransactionAcceptDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerFactoryBuilder(consumerFactory, factory);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }

    /**
     * Создает фабрику продюсера Kafka для отправки сообщений о транзакциях.
     *
     * @return {@link ProducerFactory} для {@link TransactionKafkaDto}.
     */
    @Bean
    public ProducerFactory<String, TransactionKafkaDto> transactionProducerFactory() {
        return createProducerFactory();
    }

    /**
     * Создает фабрику продюсера Kafka для отправки сообщений о подтвержденных транзакциях.
     *
     * @return {@link ProducerFactory} для {@link TransactionAcceptDto}.
     */
    @Bean
    public ProducerFactory<String, TransactionAcceptDto> transactionAcceptProducerFactory() {
        return createProducerFactory();
    }

    /**
     * Создает фабрику продюсера Kafka для отправки сообщений о результатах транзакций.
     *
     * @return {@link ProducerFactory} для {@link TransactionResultDto}.
     */
    @Bean
    public ProducerFactory<String, TransactionResultDto> transactionResultProducerFactory() {
        return createProducerFactory();
    }

    /**
     * Создает шаблон Kafka для отправки сообщений о транзакциях.
     *
     * @param producerFactory фабрика продюсеров.
     * @return {@link KafkaTemplate} для {@link TransactionKafkaDto}.
     */
    @Bean
    public KafkaTemplate<String, TransactionKafkaDto> kafkaTransactionTemplate(
            @Qualifier("transactionProducerFactory") ProducerFactory<String, TransactionKafkaDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Создает шаблон Kafka для отправки сообщений о подтвержденных транзакциях.
     *
     * @param producerFactory фабрика продюсеров.
     * @return {@link KafkaTemplate} для {@link TransactionAcceptDto}.
     */
    @Bean
    public KafkaTemplate<String, TransactionAcceptDto> kafkaTransactionAcceptTemplate(
            @Qualifier("transactionAcceptProducerFactory") ProducerFactory<String, TransactionAcceptDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Создает шаблон Kafka для отправки сообщений о результатах транзакций.
     *
     * @param producerFactory фабрика продюсеров.
     * @return {@link KafkaTemplate} для {@link TransactionResultDto}.
     */
    @Bean
    public KafkaTemplate<String, TransactionResultDto> kafkaTransactionResultTemplate(
            @Qualifier("transactionResultProducerFactory") ProducerFactory<String, TransactionResultDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Создает продюсер сообщений о транзакциях.
     *
     * @param template шаблон Kafka для отправки сообщений.
     * @return объект {@link TransactionKafkaProducer}.
     */
    @Bean
    @ConditionalOnProperty(value = "${t1.kafka.producer.enable}",
            havingValue = "true",
            matchIfMissing = true)
    public TransactionKafkaProducer producerTransaction(
            @Qualifier("kafkaTransactionTemplate") KafkaTemplate<String, TransactionKafkaDto> template) {
        template.setDefaultTopic(kafkaProperties.getTopic().getTransactionRegistration());
        return new TransactionKafkaProducer(template);
    }

    /**
     * Создает продюсер сообщений о подтвержденных транзакциях.
     *
     * @param template шаблон Kafka для отправки сообщений.
     * @return объект {@link TransactionAcceptKafkaProducer}.
     */
    @Bean
    @ConditionalOnProperty(value = "${t1.kafka.producer.enable}",
            havingValue = "true",
            matchIfMissing = true)
    public TransactionAcceptKafkaProducer producerTransactionAccept(
            @Qualifier("kafkaTransactionAcceptTemplate") KafkaTemplate<String, TransactionAcceptDto> template) {
        template.setDefaultTopic(kafkaProperties.getTopic().getTransactionAcceptStatus());
        return new TransactionAcceptKafkaProducer(template);
    }

    /**
     * Создает продюсер сообщений о результатах транзакций.
     *
     * @param template шаблон Kafka для отправки сообщений.
     * @return объект {@link TransactionResultKafkaProducer}.
     */
    @Bean
    @ConditionalOnProperty(value = "${t1.kafka.producer.enable}",
            havingValue = "true",
            matchIfMissing = true)
    public TransactionResultKafkaProducer producerTransactionResult(
            @Qualifier("kafkaTransactionResultTemplate") KafkaTemplate<String, TransactionResultDto> template) {
        template.setDefaultTopic(kafkaProperties.getTopic().getTransactionResultStatus());
        return new TransactionResultKafkaProducer(template);
    }

    /**
     * Создает и настраивает фабрику ConsumerFactory для Kafka.
     *
     * @param clazz Класс, используемый для десериализации сообщений.
     * @param <T>   Тип данных сообщения.
     * @return Настроенная фабрика потребителей.
     */
    private <T> ConsumerFactory<String, T> createConsumerFactory(Class<T> clazz) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, clazz.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.t1.java.demo.dto.kafka");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, kafkaProperties.getMax().getPartitionFetchBytes());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaProperties.getMax().getPoll().getRecords());
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaProperties.getConsumer().getSessionTimeout());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaProperties.getMax().getPoll().getIntervalMs());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaProperties.getConsumer().getHeartbeatInterval());

        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, MessageDeserializer.class.getName());
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, MessageDeserializer.class);

        DefaultKafkaConsumerFactory<String, T> factory = new DefaultKafkaConsumerFactory<>(props);
        factory.setKeyDeserializer(new StringDeserializer());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    /**
     * Создает и настраивает фабрику ProducerFactory для Kafka.
     *
     * @param <T> Тип данных сообщения.
     * @return Настроенная фабрика производителей.
     */
    private <T> ProducerFactory<String, T> createProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServer());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Настраивает {@link ConcurrentKafkaListenerContainerFactory} с указанной {@link ConsumerFactory}.
     *
     * @param consumerFactory фабрика консьюмеров {@link ConsumerFactory}, используемая для обработки сообщений.
     * @param factory         контейнерная фабрика {@link ConcurrentKafkaListenerContainerFactory}, которая будет настроена.
     */
    private <T> void kafkaListenerFactoryBuilder(ConsumerFactory<String, T> consumerFactory,
                                                 ConcurrentKafkaListenerContainerFactory<String, T> factory) {
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setPollTimeout(Long.parseLong(kafkaProperties.getListener().getPollTimeout()));
        factory.getContainerProperties().setMicrometerEnabled(true);
    }
}
