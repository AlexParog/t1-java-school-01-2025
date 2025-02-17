package ru.t1.java.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * Исключение, выбрасываемое в случае, если счет не открыт или не активен для выполнения транзакций.
 * <p>
 * Расширяет {@link RuntimeException} и устанавливает статус ответа HTTP 409 (CONFLICT).
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class AccountNotOpenException extends RuntimeException {
    /**
     * Создает исключение с заданным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public AccountNotOpenException(String message) {
        super(message);
    }

    /**
     * Создает исключение с заданным сообщением и аргументами для форматирования.
     *
     * @param message Сообщение об ошибке с плейсхолдерами.
     * @param args    Аргументы для форматирования сообщения.
     */
    public AccountNotOpenException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    /**
     * Возвращает поставщика исключения с заданным сообщением и аргументами для форматирования.
     *
     * @param message Сообщение об ошибке с плейсхолдерами.
     * @param args    Аргументы для форматирования сообщения.
     * @return Поставщик исключения AccountNotOpenException.
     */
    public static Supplier<NotFoundException> accountNotOpenException(String message, Object... args) {
        return () -> new NotFoundException(message, args);
    }

    /**
     * Возвращает поставщика исключения с заданным сообщением.
     *
     * @param message Сообщение об ошибке.
     * @return Поставщик исключения AccountNotOpenException.
     */
    public static Supplier<NotFoundException> accountNotOpenException(String message) {
        return () -> new NotFoundException(message);
    }
}
