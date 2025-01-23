package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.Arrays;

/**
 * Аспект для логирования ошибок, возникающих при работе с источниками данных.
 * <p>
 * Перехватывает исключения, помеченные аннотацией {@link LogDataSourceError}, логирует их и сохраняет в базу данных.
 */
@Slf4j
@RequiredArgsConstructor
// @Async
@Aspect
@Component
public class LoggingDataSourceErrorAspect {

    /**
     * Сервис для сохранения логов ошибок источников данных.
     */
    private final DataSourceErrorLogService dataSourceErrorLogService;

    /**
     * Логирует ошибки, возникающие в методах, помеченных аннотацией {@link LogDataSourceError}.
     * <p>
     * Извлекает сообщение об ошибке, стек вызовов и сигнатуру метода, сохраняет в базу данных и выводит в лог.
     *
     * @param joinPoint Точка соединения (метод), в котором произошло исключение.
     * @param exception  Возникшее исключение.
     */
    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "exception")
    public void logDataSourceError(JoinPoint joinPoint, Throwable exception) {
        DataSourceErrorLog dataSourceErrorLog = new DataSourceErrorLog();
        dataSourceErrorLog.setMessage(exception.getMessage());
        dataSourceErrorLog.setStacktrace(Arrays.toString(exception.getStackTrace()));

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // полное описание метода (модификаторы доступа, возвращаемый тип, имя класса, имя метода и типы параметров)
        String methodName = signature.toLongString();
        dataSourceErrorLog.setMethodSignature(methodName);

        dataSourceErrorLogService.saveLog(dataSourceErrorLog);
        log.error("Ошибка при работе с источником данных в методе {}: {}", methodName, exception.getMessage());
    }
}
