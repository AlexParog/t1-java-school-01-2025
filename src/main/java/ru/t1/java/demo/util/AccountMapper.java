package ru.t1.java.demo.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.t1.java.demo.dto.AccountRequestDto;
import ru.t1.java.demo.dto.AccountResponseDto;
import ru.t1.java.demo.model.Account;

/**
 * Маппер для преобразования между сущностью {@link ru.t1.java.demo.model.Account} и DTO объектами.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {TransactionMapper.class})
public interface AccountMapper {
    /**
     * Преобразует сущность {@link Account} в объект {@link AccountResponseDto}.
     *
     * @param account объект счета.
     * @return DTO счета.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "clientId", expression = "java(account.getClient().getId())")
    @Mapping(target = "accountTypeEnum", source = "accountTypeEnum")
    @Mapping(target = "accountTransactions", source = "accountTransactions")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "archiveDate", source = "archiveDate")
    AccountResponseDto toAccountResponseDto(Account account);

    /**
     * Преобразует сущность {@link AccountRequestDto} в объект {@link Account}.
     *
     * @param accountRequestDto DTO с данными для создания счета.
     * @return объект сущности счета.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "accountTypeEnum", source = "accountTypeEnum")
    @Mapping(target = "accountTransactions", source = "accountTransactions")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "archiveDate", source = "archiveDate")
    Account toAccount(AccountRequestDto accountRequestDto);

    /**
     * Обновляет существующий объект {@link Account} на основе данных из {@link AccountRequestDto}.
     *
     * @param accountRequestDto DTO с новыми данными счета.
     * @param account           объект счета, который необходимо обновить.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "client", ignore = true)
    void updateAccountFromDto(AccountRequestDto accountRequestDto, @MappingTarget Account account);
}
