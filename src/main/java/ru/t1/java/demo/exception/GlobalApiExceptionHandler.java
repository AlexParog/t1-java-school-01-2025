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

    /**
     * Обработчик исключения {@link AccountNotOpenException}.
     * Возвращает статус 409 (CONFLICT) и сообщение об ошибке.
     *
     * @param accountNotOpenException Исключение {@link AccountNotOpenException}.
     * @return Ответ с сообщением об ошибке и статусом 409.
     */
    @ExceptionHandler(AccountNotOpenException.class)
    public ResponseEntity<ErrorMessage> accountNotOpenException(AccountNotOpenException accountNotOpenException) {
        log.error(accountNotOpenException.getMessage(), accountNotOpenException);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(accountNotOpenException.getMessage()));
    }

    /**
     * Обработчик исключения {@link InsufficientFundsException}.
     * Возвращает статус 402 (PAYMENT_REQUIRED) и сообщение об ошибке.
     *
     * @param insufficientFundsException Исключение {@link InsufficientFundsException}.
     * @return Ответ с сообщением об ошибке и статусом 402.
     */
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorMessage> insufficientFundsException(InsufficientFundsException insufficientFundsException) {
        log.error(insufficientFundsException.getMessage(), insufficientFundsException);
        return ResponseEntity
                .status(HttpStatus.PAYMENT_REQUIRED)
                .body(new ErrorMessage(insufficientFundsException.getMessage()));
    }

    /**
     * Обработчик исключения {@link UnknownTransactionStatusException}.
     * Возвращает статус 400 (BAD_REQUEST) и сообщение об ошибке.
     *
     * @param unknownTransactionStatusException Исключение {@link UnknownTransactionStatusException}.
     * @return Ответ с сообщением об ошибке и статусом 400.
     */
    @ExceptionHandler(UnknownTransactionStatusException.class)
    public ResponseEntity<ErrorMessage> unknownTransactionStatusException(
            UnknownTransactionStatusException unknownTransactionStatusException) {
        log.error(unknownTransactionStatusException.getMessage(), unknownTransactionStatusException);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(unknownTransactionStatusException.getMessage()));
    }

    /**
     * Обработчик исключения {@link Exception}.
     * Возвращает статус 500 (INTERNAL_SERVER_ERROR) и сообщение об ошибке.
     *
     * @param unknownException Исключение {@link Exception}.
     * @return Ответ с сообщением об ошибке и статусом 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> unknownException(Exception unknownException) {
        log.error(unknownException.getMessage(), unknownException);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessage(unknownException.getMessage()));
    }
}
