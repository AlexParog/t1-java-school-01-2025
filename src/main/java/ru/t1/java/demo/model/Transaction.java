package ru.t1.java.demo.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.format.annotation.DateTimeFormat;
import ru.t1.java.demo.model.enums.TransactionStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс представляет сущность "Транзакции".
 */
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction extends AbstractPersistable<Long> {

    /**
     * Уникальный идентификатор транзакции.
     */
    @NotNull
    @Column(name = "transaction_id")
    private UUID transactionId;

    /**
     * ID счета.
     */
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * Статус транзакции.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatusEnum status;

    /**
     * Сумма транзакции.
     */
    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * Время транзакции.
     */
    @NotNull
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "time_of_purchase", nullable = false)
    private LocalDateTime timeOfPurchase;

    /**
     * Дата архивирования товара.
     */
    @Nullable
    @Column(name = "archive_date")
    private LocalDateTime archiveDate;

    /**
     * Проверяет, является ли транзакция "удалённой".
     * Транзакция считается удалённой, если поле {@code archiveDate} не равно {@code null}.
     *
     * @return {@code true}, если транзакция удалена, иначе {@code false}
     */
    public boolean isDeleted() {
        return archiveDate != null;
    }
}
