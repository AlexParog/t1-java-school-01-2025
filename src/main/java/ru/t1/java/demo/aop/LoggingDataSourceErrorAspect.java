package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.MetricMessageDto;
import ru.t1.java.demo.kafka.producer.MetricKafkaProducer;
import ru.t1.java.demo.model.enums.MetricMessageTypeEnum;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.Arrays;

/**
 * Аспект для логирования ошибок, возникающих при работе с источниками данных,
 * и отправки метрик в Kafka.
 * <p>
 * Перехватывает исключения, возникающие в методах, помеченных аннотацией {@link LogDataSourceError},
 * логирует их, отправляет информацию в Kafka и, в случае неудачи, сохраняет в базу данных.
 */
@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class LoggingDataSourceErrorAspect {

    /**
     * Сервис для сохранения логов ошибок источников данных.
     */
    private final DataSourceErrorLogService dataSourceErrorLogService;
    /**
     * Продюсер для отправки метрик в Kafka.
     */
    private final MetricKafkaProducer metricKafkaProducer;

    /**
     * Обрабатывает исключения, возникшие в методах, помеченных аннотацией {@link LogDataSourceError}.
     * <p>
     * Логирует ошибку, формирует сообщение с деталями исключения и отправляет его в Kafka.
     * Если отправка в Kafka не удалась, сохраняет лог ошибки в базе данных.
     *
     * @param joinPoint Точка соединения (метод), в котором произошло исключение.
     * @param exception Исключение, которое было выброшено.
     */
    @AfterThrowing(pointcut = "@annotation(LogDataSourceError)", throwing = "exception")
    public void logDataSourceError(JoinPoint joinPoint, Throwable exception) {
        log.error("Ошибка при работе с источником данных в методе {}: {}",
                joinPoint.getSignature().getName(),
                exception.getMessage());

        MetricMessageDto metricMessageDto = new MetricMessageDto();
        metricMessageDto.setType(MetricMessageTypeEnum.DATA_SOURCE);
        metricMessageDto.setMessage(exception.getMessage());
        metricMessageDto.setMethodSignature(joinPoint.getSignature().toLongString());
        metricMessageDto.setStacktrace(Arrays.toString(exception.getStackTrace()));

        try {
            log.info("Отправка метрик: {} в Kafka", metricMessageDto);
            metricKafkaProducer.send(metricMessageDto);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения в Kafka", e);
            dataSourceErrorLogService.saveLog(exception, joinPoint.getSignature().toLongString());
        }
    }
}