package ru.t1.java.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MetricMessageTypeEnum {
    /**
     * Ошибка, связанная с базой данных.
     */
    DATA_SOURCE("DATA_SOURCE"),

    /**
     * Ошибка, связанная с работой метода.
     */
    METRICS("METRICS");

    /**
     * Строковое значение метрики.
     */
    @JsonValue
    private final String value;

    /**
     * Конструктор для создания типа метрики с указанным строковым значением.
     *
     * @param value строковое представление счета
     */
    MetricMessageTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Преобразует строковое значение в соответствующий тип метрики.
     *
     * @param value строковое представление метрики
     * @return объект {@link MetricMessageTypeEnum}, соответствующий строковому значению,
     * или {@code null}, если значение некорректно
     */
    @JsonCreator
    public MetricMessageTypeEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.toLowerCase().equals(value))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает строковое представление типа метрики.
     *
     * @return строковое значение метрики
     */
    @Override
    public String toString() {
        return value;
    }
}