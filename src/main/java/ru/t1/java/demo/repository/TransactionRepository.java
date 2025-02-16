package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.TransactionStatusEnum;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Репозиторий для работы с сущностью {@link Transaction}
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findById(Long id);

    Optional<Transaction> findTransactionByTransactionId(UUID transactionId);

    @Transactional
    @Modifying
    @Query("UPDATE Transaction t SET t.status = ?2 WHERE t.transactionId = ?1")
    void updateStatusByTransactionId(UUID transactionUuid, TransactionStatusEnum status);
}
