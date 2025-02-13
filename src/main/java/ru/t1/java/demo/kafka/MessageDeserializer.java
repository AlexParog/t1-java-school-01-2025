package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Компонент для десериализации сообщений Kafka.
 * Преобразует байтовые данные в объекты указанного типа.
 *
 * @param <T> тип объекта, в который десериализуются данные.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageDeserializer<T> extends JsonDeserializer<T> {

//    private final ObjectMapper objectMapper;

    /**
     * Преобразует байтовый массив в строку.
     *
     * @param data байтовый массив.
     * @return строка, полученная из байтового массива.
     */
    private static String getMessage(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Десериализует сообщение Kafka с учётом заголовков.
     *
     * @param topic   название топика.
     * @param headers заголовки сообщения.
     * @param data    байтовый массив с данными.
     * @return десериализованный объект.
     */
    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {

//            objectMapper.readValue(getMessage(data), ClientDto.class);

            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            log.warn("Произошла ошибка во время десериализации сообщения {}", new String(data, StandardCharsets.UTF_8), e);
            return null;
        }
    }

    /**
     * Десериализует сообщение Kafka.
     *
     * @param topic название топика.
     * @param data  байтовый массив с данными.
     * @return десериализованный объект.
     * @throws SerializationException если произошла ошибка десериализации.
     */
    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data == null) {
                log.warn("Получено пустое сообщение");
                return null;
            }

            return super.deserialize(topic, data);
        } catch (Exception e) {
            log.error("Ошибка десериализации сообщения: {}", new String(data, StandardCharsets.UTF_8), e);
            throw new SerializationException("Ошибка десериализации", e);
        }
    }
}