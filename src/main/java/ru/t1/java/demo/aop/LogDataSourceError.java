package ru.t1.java.demo.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки методов, ошибки которых должны быть залогированы.
 * <p>
 * Используется в сочетании с аспектом {@link LoggingDataSourceErrorAspect}
 * для автоматического логирования исключений, возникающих при работе с источниками данных.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogDataSourceError {
}
