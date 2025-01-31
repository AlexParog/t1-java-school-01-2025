package ru.t1.java.demo.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для измерения времени выполнения метода и отправки метрики в Kafka,
 * если время выполнения превышает заданное пороговое значение.
 * <p>
 * Используется в сочетании с {@link MetricAspect}, который перехватывает вызовы методов,
 * аннотированных {@code @Metric}, и измеряет их время выполнения.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Metric {
    /**
     * Пороговое значение времени выполнения метода в миллисекундах.
     * Если выполнение метода превышает указанное значение, информация о метрике будет отправлена в Kafka.
     *
     * @return максимальное допустимое время выполнения метода в миллисекундах.
     */
    long value();
}