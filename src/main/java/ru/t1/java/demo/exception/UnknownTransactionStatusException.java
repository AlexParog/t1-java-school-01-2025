package ru.t1.java.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * Исключение, выбрасываемое в случае, если статус транзакции неизвестен или не поддерживается.
 * <p>
 * Расширяет {@link RuntimeException} и устанавливает статус ответа HTTP 400 (BAD_REQUEST).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownTransactionStatusException extends RuntimeException {
    /**
     * Создает исключение с заданным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public UnknownTransactionStatusException(String message) {
        super(message);
    }

    /**
     * Создает исключение с заданным сообщением и аргументами для форматирования.
     *
     * @param message Сообщение об ошибке с плейсхолдерами.
     * @param args    Аргументы для форматирования сообщения.
     */
    public UnknownTransactionStatusException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    /**
     * Возвращает поставщика исключения с заданным сообщением и аргументами для форматирования.
     *
     * @param message Сообщение об ошибке с плейсхолдерами.
     * @param args    Аргументы для форматирования сообщения.
     * @return Поставщик исключения UnknownTransactionStatusException.
     */
    public static Supplier<NotFoundException> unknownTransactionStatusException(String message, Object... args) {
        return () -> new NotFoundException(message, args);
    }

    /**
     * Возвращает поставщика исключения с заданным сообщением.
     *
     * @param message Сообщение об ошибке.
     * @return Поставщик исключения UnknownTransactionStatusException.
     */
    public static Supplier<NotFoundException> unknownTransactionStatusException(String message) {
        return () -> new NotFoundException(message);
    }
}
