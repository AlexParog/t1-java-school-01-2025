package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.enums.AccountStatusEnum;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Репозиторий для работы с сущностью {@link Account}
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findById(Long id);

    Optional<Account> findAccountByAccountId(UUID accountId);

    @Transactional
    @Modifying
    @Query("UPDATE Account a SET a.status = ?2 WHERE a.accountId = ?1")
    void updateStatusByAccountId(UUID accountId, AccountStatusEnum status);

    @Transactional
    @Modifying
    @Query("UPDATE Account a SET a.frozenAmount = ?2 WHERE a.accountId = ?1")
    void updateFrozenAmountByAccountId(UUID accountId, BigDecimal frozenAmount);

    @Transactional
    @Modifying
    @Query("UPDATE Account a SET a.balance = ?2 WHERE a.accountId = ?1")
    void updateBalanceByAccountId(UUID accountId, BigDecimal balance);
}
