package ru.t1.java.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * Enum, представляющий статус транзакции {@link ru.t1.java.demo.model.Transaction}.
 */
@Getter
public enum TransactionStatusEnum {
    /**
     * Транзакция принята.
     */
    ACCEPTED("ACCEPTED"),

    /**
     * Транзакция отклонена.
     */
    REJECTED("REJECTED"),

    /**
     * Транзакция заблокирована.
     */
    BLOCKED("BLOCKED"),

    /**
     * Транзакция отменена.
     */
    CANCELLED("CANCELLED"),

    /**
     * Транзакция запрошена.
     */
    REQUESTED("REQUESTED");

    /**
     * Строковое значение счета.
     */
    @JsonValue
    private final String value;

    /**
     * Конструктор для создания статуса транзакции с указанным строковым значением.
     *
     * @param value строковое представление счета
     */
    TransactionStatusEnum(String value) {
        this.value = value;
    }

    /**
     * Преобразует строковое значение в соответствующий статус транзакции.
     *
     * @param value строковое представление статуса транзакции
     * @return объект {@link TransactionStatusEnum}, соответствующий строковому значению,
     * или {@code null}, если значение некорректно
     */
    @JsonCreator
    public TransactionStatusEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.toLowerCase().equals(value))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает строковое представление статуса транзакции.
     *
     * @return строковое значение статуса транзакции
     */
    @Override
    public String toString() {
        return value;
    }
}
