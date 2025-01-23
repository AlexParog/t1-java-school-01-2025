package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO-ответа для передачи информации {@link ru.t1.java.demo.model.Transaction}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResponseDto implements Serializable {
    /**
     * ID транзакции.
     */
    private Long id;

    /**
     * ID счета.
     */
    @NotNull
    @JsonProperty("account_id")
    private Long accountId;

    /**
     * Сумма транзакции.
     */
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Сумма транзакции должна быть больше 0")
    @Digits(integer = 10, fraction = 2, message = "Сумма транзакции должна быть денежным значением")
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * Время транзакции.
     */
    @NotNull
    @JsonProperty("time_of_purchase")
    private LocalDateTime timeOfPurchase;

    /**
     * Дата архивирования товара.
     */
    @Nullable
    @JsonProperty("archive_date")
    private LocalDateTime archiveDate;
}
