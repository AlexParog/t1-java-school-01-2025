package ru.t1.java.demo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Глобальный обработчик исключений API.
 * <p>
 * Перехватывает исключения, возникающие в приложении, и обрабатывает их, возвращая клиенту
 * соответствующее сообщение об ошибке и статус HTTP.
 */
@Slf4j
@RestControllerAdvice
public class GlobalApiExceptionHandler {
    /**
     * Обработчик исключения {@link NotFoundException}.
     * Возвращает статус 404 (NOT_FOUND) и сообщение об ошибке.
     *
     * @param notFoundException Исключение {@link NotFoundException}.
     * @return Ответ с сообщением об ошибке и статусом 404.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorMessage> notFoundException(NotFoundException notFoundException) {
        log.error(notFoundException.getMessage(), notFoundException);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(notFoundException.getMessage()));
    }
}
