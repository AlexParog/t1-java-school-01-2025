package ru.t1.java.demo.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.dto.kafka.MetricMessageDto;
import ru.t1.java.demo.kafka.producer.MetricKafkaProducer;
import ru.t1.java.demo.model.enums.MetricMessageTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Аспект для мониторинга времени выполнения методов, помеченных аннотацией {@link Metric}.
 * <p>
 * Перехватывает выполнение методов, измеряет их время выполнения и отправляет информацию в Kafka,
 * если оно превышает заданное пороговое значение.
 */
@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class MetricAspect {

    /**
     * Сообщение о превышении времени выполнения метода.
     */
    private static final String EXECUTION_TIME_EXCEEDED = "Method execution time exceeded";

    /**
     * Продюсер для отправки метрик в Kafka.
     */
    private final MetricKafkaProducer metricKafkaProducer;

    /**
     * Измеряет время выполнения метода, аннотированного {@link Metric}, и отправляет метрику в Kafka,
     * если оно превышает заданное пороговое значение.
     *
     * @param joinPoint точка соединения, содержащая информацию о вызванном методе.
     * @param metric    аннотация с пороговым значением времени выполнения.
     * @return результат выполнения перехваченного метода.
     * @throws Throwable если вызываемый метод выбрасывает исключение.
     */
    @Around("@annotation(metric)")
    public Object measureMethodExecutionTime(ProceedingJoinPoint joinPoint, Metric metric) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // время выполнения превышает порог, отправить метрику
            if (executionTime > metric.value()) {
                MetricMessageDto metricMessageDto = buildMetricMessage(joinPoint, executionTime, null);
                log.info("Отправка метрик: {} в Kafka", metricMessageDto);
                metricKafkaProducer.send(metricMessageDto);
            }
            return result;
        } catch (Throwable throwable) {
            long executionTime = System.currentTimeMillis() - startTime;
            // фиксируем метрику для методов, завершившихся с выбросом исключения.
            MetricMessageDto metricMessageDto = buildMetricMessage(joinPoint, executionTime, throwable);
            log.error("Метод завершился с ошибкой, отправка метрик: {} в Kafka", metricMessageDto, throwable);
            metricKafkaProducer.send(metricMessageDto);

            // пробрасываем исключение дальше
            throw throwable;
        }
    }

    /**
     * Вспомогательный метод для формирования метрики.
     *
     * @param joinPoint     точка соединения метода
     * @param executionTime время выполнения метода в миллисекундах
     * @param throwable     выброшенное исключение, если оно произошло, иначе null
     * @return заполненный объект метрики
     */
    private MetricMessageDto buildMetricMessage(ProceedingJoinPoint joinPoint, long executionTime, Throwable throwable) {
        MetricMessageDto metricMessageDto = new MetricMessageDto();
        metricMessageDto.setType(MetricMessageTypeEnum.METRICS);

        if (throwable != null) {
            metricMessageDto.setMessage("Method execution failed: " + throwable.getMessage());
        } else {
            metricMessageDto.setMessage(EXECUTION_TIME_EXCEEDED);
        }
        metricMessageDto.setMethodSignature(joinPoint.getSignature().toLongString());
        metricMessageDto.setExecutionTimeMs(executionTime);
        metricMessageDto.setParameters(getMethodParameters(joinPoint));
        return metricMessageDto;
    }

    /**
     * Извлекает параметры метода, переданные при его вызове.
     *
     * @param joinPoint точка соединения, содержащая информацию о вызванном методе.
     * @return карта с именами параметров и их значениями.
     */
    private Map<String, Object> getMethodParameters(ProceedingJoinPoint joinPoint) {
        Map<String, Object> parameters = new HashMap<>();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] parameterValues = joinPoint.getArgs();

        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                parameters.put(parameterNames[i], parameterValues[i]);
            }
        }

        return parameters;
    }
}
