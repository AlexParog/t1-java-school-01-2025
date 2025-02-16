package ru.t1.java.demo.dto.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.t1.java.demo.model.enums.TransactionStatusEnum;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionResultDto {
    /**
     * Уникальный номер транзакции.
     */
    private UUID transactionId;

    /**
     * Уникальный номер счета.
     */
    private UUID accountId;

    /**
     * Статус транзакции.
     */
    private TransactionStatusEnum status;
}
