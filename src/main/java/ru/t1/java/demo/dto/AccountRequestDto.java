package ru.t1.java.demo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.t1.java.demo.model.Transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO-запроса для создания и обновления {@link ru.t1.java.demo.model.Account}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountRequestDto implements Serializable {

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
     * Совершенные транзакции по счету.
     */
    @NotNull
    @JsonProperty("account_transactions")
    private Set<Transaction> accountTransactions = new LinkedHashSet<>();

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
