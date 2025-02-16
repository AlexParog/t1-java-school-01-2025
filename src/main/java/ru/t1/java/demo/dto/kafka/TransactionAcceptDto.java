package ru.t1.java.demo.dto.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionAcceptDto {
    /**
     * Уникальный номер клиента.
     */
    @NotNull
    @JsonProperty("client_id")
    private UUID clientId;

    /**
     * Уникальный номер счета.
     */
    @NotNull
    @JsonProperty("account_id")
    private UUID accountId;

    /**
     * Уникальный номер транзакции.
     */
    @NotNull
    @JsonProperty("transaction_id")
    private UUID transactionId;

    /**
     *
     */
    @NotNull
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    /**
     * Сумма транзакции.
     */
    @NotNull
    @Digits(integer = 10, fraction = 2, message = "Сумма транзакции должна быть денежным значением")
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * Баланс счета.
     */
    @NotNull
    @PositiveOrZero
    @Digits(integer = 10, fraction = 2, message = "Сумма баланса должна быть денежным значением")
    @JsonProperty("balance")
    private BigDecimal balance;
}
