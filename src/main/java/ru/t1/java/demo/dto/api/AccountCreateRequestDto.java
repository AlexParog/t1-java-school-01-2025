package ru.t1.java.demo.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO-запрос для создания нового счета.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountCreateRequestDto implements Serializable {

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
    @Digits(integer = 10, fraction = 2)
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;
}
