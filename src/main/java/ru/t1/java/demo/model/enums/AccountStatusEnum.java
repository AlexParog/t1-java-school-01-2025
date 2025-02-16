package ru.t1.java.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

/**
 * Enum, представляющий статус счета {@link ru.t1.java.demo.model.Account}.
 */
@Getter
public enum AccountStatusEnum {
    /**
     * Счет открыт.
     */
    OPEN("OPEN"),

    /**
     * Счет арестован.
     */
    ARRESTED("ARRESTED"),

    /**
     * Счет заблокирован.
     */
    BLOCKED("BLOCKED"),

    /**
     * Счет закрыт.
     */
    CLOSED("CLOSED");

    /**
     * Строковое значение статуса счета.
     */
    @JsonValue
    private final String value;

    /**
     * Конструктор для создания статуса счета с указанным строковым значением.
     *
     * @param value строковое представление счета
     */
    AccountStatusEnum(String value) {
        this.value = value;
    }

    /**
     * Преобразует строковое значение в соответствующий статус счета.
     *
     * @param value строковое представление статуса счета
     * @return объект {@link AccountStatusEnum}, соответствующий строковому значению,
     * или {@code null}, если значение некорректно
     */
    @JsonCreator
    public AccountStatusEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.toLowerCase().equals(value))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает строковое представление статуса счета.
     *
     * @return строковое значение статуса счета
     */
    @Override
    public String toString() {
        return value;
    }
}
