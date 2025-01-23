package ru.t1.java.demo.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.t1.java.demo.dto.TransactionRequestDto;
import ru.t1.java.demo.dto.TransactionResponseDto;
import ru.t1.java.demo.model.Transaction;

/**
 * Маппер для преобразования между сущностью {@link Transaction} и DTO объектами.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    /**
     * Преобразует сущность {@link Transaction} в объект {@link TransactionResponseDto}.
     *
     * @param transaction объект транзакции.
     * @return DTO транзакции.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "accountId", expression = "java(transaction.getAccount().getId())")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "timeOfPurchase", source = "timeOfPurchase")
    @Mapping(target = "archiveDate", source = "archiveDate")
    TransactionResponseDto toTransactionResponseDto(Transaction transaction);

    /**
     * Преобразует сущность {@link TransactionRequestDto} в объект {@link Transaction}.
     *
     * @param transactionRequestDto DTO с данными для создания транзакции.
     * @return объект сущности транзакции.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "timeOfPurchase", source = "timeOfPurchase")
    @Mapping(target = "archiveDate", source = "archiveDate")
    Transaction toTransaction(TransactionRequestDto transactionRequestDto);

    /**
     * Обновляет существующий объект {@link Transaction} на основе данных из {@link TransactionRequestDto}.
     *
     * @param transactionRequestDto DTO с новыми данными транзакции.
     * @param transaction           объект транзакции, который необходимо обновить.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "account", ignore = true)
    void updateTransactionFromDto(TransactionRequestDto transactionRequestDto, @MappingTarget Transaction transaction);
}
