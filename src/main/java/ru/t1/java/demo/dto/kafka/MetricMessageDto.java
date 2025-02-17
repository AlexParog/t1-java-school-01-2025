package ru.t1.java.demo.dto.kafka;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.java.demo.model.enums.MetricMessageTypeEnum;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO для передачи информации о метриках через Kafka.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricMessageDto {
    /**
     * Тип метрики.
     */
    @NotNull
    @JsonProperty("type")
    private MetricMessageTypeEnum type;

    /**
     * Сообщение.
     */
    @NotNull
    @JsonProperty("message")
    private String message;

    /**
     * Время ошибки.
     */
    @JsonProperty("timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Сигнатура метода.
     */
    @NotNull
    @JsonProperty("method_signature")
    private String methodSignature;

    /**
     * Параметры метода.
     */
    @Nullable
    @JsonProperty("parameters")
    private Map<String, Object> parameters;

    /**
     * Время работы метода.
     */
    @NotNull
    @JsonProperty("execution_time_ms")
    private Long executionTimeMs;

    /**
     * Стектрейс ошибок.
     */
    @Nullable
    @JsonProperty("stacktrace")
    private String stacktrace;
}