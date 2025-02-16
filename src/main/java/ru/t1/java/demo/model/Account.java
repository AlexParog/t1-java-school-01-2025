package ru.t1.java.demo.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;
import ru.t1.java.demo.model.enums.AccountStatusEnum;
import ru.t1.java.demo.model.enums.AccountTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Класс представляет сущность "Счета пользователя".
 */
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class Account extends AbstractPersistable<Long> {

    /**
     * Уникальный идентификатор счета.
     */
    @NotNull
    @Column(name = "account_id")
    private UUID accountId;

    /**
     * ID Клиента, которому принадлежит счет.
     */
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    /**
     * Совершенные транзакции по счету.
     */
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
    private Set<Transaction> accountTransactions = new LinkedHashSet<>();

    /**
     * Статус счета.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatusEnum status;

    /**
     * Тип счета.
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountTypeEnum accountTypeEnum;

    /**
     * Баланс счета.
     */
    @NotNull
    @Digits(integer = 10, fraction = 2)
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    /**
     * Замороженная сумма на счету.
     */
    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    @Column(name = "frozen_amount")
    private BigDecimal frozenAmount;

    /**
     * Дата архивирования товара.
     */
    @Nullable
    @Column(name = "archive_date")
    private LocalDateTime archiveDate;

    /**
     * Проверяет, является ли счет "удалённым".
     * Счет считается удалённым, если поле {@code archiveDate} не равно {@code null}.
     *
     * @return {@code true}, если счет удалён, иначе {@code false}
     */
    public boolean isDeleted() {
        return archiveDate != null;
    }
}