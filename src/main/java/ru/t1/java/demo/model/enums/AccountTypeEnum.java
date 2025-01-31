package ru.t1.java.demo.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import ru.t1.java.demo.model.Account;

import java.util.Arrays;

/**
 * Enum, представляющий тип счета {@link Account}.
 */
@Getter
public enum AccountTypeEnum {
    /**
     * Дебетовый счет.
     */
    DEBIT("DEBIT"),

    /**
     * Кредитный счет.
     */
    CREDIT("CREDIT");

    /**
     * Строковое значение счета.
     */
    @JsonValue
    private final String value;

    /**
     * Конструктор для создания типа счета с указанным строковым значением.
     *
     * @param value строковое представление счета
     */
    AccountTypeEnum(String value) {
        this.value = value;
    }

    /**
     * Преобразует строковое значение в соответствующий тип счета.
     *
     * @param value строковое представление счета
     * @return объект {@link AccountTypeEnum}, соответствующий строковому значению,
     * или {@code null}, если значение некорректно
     */
    @JsonCreator
    public AccountTypeEnum fromString(String value) {
        return Arrays.stream(values())
                .filter(status -> status.value.toLowerCase().equals(value))
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает строковое представление счета.
     *
     * @return строковое значение счета
     */
    @Override
    public String toString() {
        return value;
    }
}
