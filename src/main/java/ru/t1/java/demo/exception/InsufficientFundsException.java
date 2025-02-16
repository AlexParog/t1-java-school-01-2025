package ru.t1.java.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * Исключение, выбрасываемое в случае, если на счете недостаточно средств для выполнения транзакции.
 * <p>
 * Расширяет {@link RuntimeException} и устанавливает статус ответа HTTP 402 (PAYMENT_REQUIRED).
 */
@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class InsufficientFundsException extends RuntimeException {
    /**
     * Создает исключение с заданным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public InsufficientFundsException(String message) {
        super(message);
    }

    /**
     * Создает исключение с заданным сообщением и аргументами для форматирования.
     *
     * @param message Сообщение об ошибке с плейсхолдерами.
     * @param args    Аргументы для форматирования сообщения.
     */
    public InsufficientFundsException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    /**
     * Возвращает поставщика исключения с заданным сообщением и аргументами для форматирования.
     *
     * @param message Сообщение об ошибке с плейсхолдерами.
     * @param args    Аргументы для форматирования сообщения.
     * @return Поставщик исключения InsufficientFundsException.
     */
    public static Supplier<NotFoundException> insufficientFundsException(String message, Object... args) {
        return () -> new NotFoundException(message, args);
    }

    /**
     * Возвращает поставщика исключения с заданным сообщением.
     *
     * @param message Сообщение об ошибке.
     * @return Поставщик исключения InsufficientFundsException.
     */
    public static Supplier<NotFoundException> insufficientFundsException(String message) {
        return () -> new NotFoundException(message);
    }
}
