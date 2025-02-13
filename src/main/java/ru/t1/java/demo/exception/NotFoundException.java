package ru.t1.java.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.text.MessageFormat;
import java.util.function.Supplier;

/**
 * Исключение, выбрасываемое в случае, если сущность не найдена.
 * <p>
 * Расширяет {@link RuntimeException} и устанавливает статус ответа HTTP 404 (NOT_FOUND).
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    /**
     * Создает исключение с заданным сообщением.
     *
     * @param message Сообщение об ошибке.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Создает исключение с заданным сообщением и аргументами для форматирования.
     *
     * @param message Сообщение об ошибке с плейсхолдерами.
     * @param args    Аргументы для форматирования сообщения.
     */
    public NotFoundException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    /**
     * Возвращает поставщика исключения с заданным сообщением и аргументами для форматирования.
     *
     * @param message Сообщение об ошибке с плейсхолдерами.
     * @param args    Аргументы для форматирования сообщения.
     * @return Поставщик исключения NotFoundException.
     */
    public static Supplier<NotFoundException> notFoundException(String message, Object... args) {
        return () -> new NotFoundException(message, args);
    }

    /**
     * Возвращает поставщика исключения с заданным сообщением.
     *
     * @param message Сообщение об ошибке.
     * @return Поставщик исключения NotFoundException.
     */
    public static Supplier<NotFoundException> notFoundException(String message) {
        return () -> new NotFoundException(message);
    }
}
