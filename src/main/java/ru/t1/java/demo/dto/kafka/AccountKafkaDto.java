package ru.t1.java.demo.dto.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO для передачи информации о счете через Kafka.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountKafkaDto {
    /**
     * ID Клиента, которому принадлежит счет.
     */
    @NotNull
    @JsonProperty("client_id")
    private Long clientId;

    /**
     * Тип счета.
     */
    @NotNull
    @JsonProperty("account_type_enum")
    private String accountTypeEnum;

    /**
     * Баланс счета.
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Балан может быть больше 0")
    @Digits(integer = 10, fraction = 2, message = "Баланс должен быть денежным значением")
    @JsonProperty("balance")
    private BigDecimal balance;

    /**
     * Дата архивирования товара.
     */
    @Nullable
    @JsonProperty("archive_date")
    private LocalDateTime archiveDate;
}