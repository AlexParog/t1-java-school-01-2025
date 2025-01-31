package ru.t1.java.demo.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import ru.t1.java.demo.dto.api.AccountCreateRequestDto;
import ru.t1.java.demo.dto.api.AccountResponseDto;
import ru.t1.java.demo.dto.api.AccountUpdateRequestDto;
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
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "archiveDate", source = "archiveDate")
    AccountResponseDto toAccountResponseDto(Account account);

    /**
     * Преобразует сущность {@link AccountCreateRequestDto} в объект {@link Account}.
     *
     * @param accountCreateRequestDto Dto с данными для создания нового счета.
     * @return новый объект счета.
     */
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "accountTransactions", ignore = true)
    @Mapping(target = "archiveDate", ignore = true)
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "accountTypeEnum", source = "accountTypeEnum")
    Account toAccountAfterCreate(AccountCreateRequestDto accountCreateRequestDto);


    /**
     * Обновляет существующий объект {@link Account} на основе данных из {@link AccountUpdateRequestDto}.
     *
     * @param accountUpdateRequestDto DTO с новыми данными счета.
     * @param account                 объект счета, который необходимо обновить.
     */
    @Mapping(target = "client", ignore = true)
    @Mapping(target = "accountTransactions", ignore = true)
    void updateAccountFromDto(AccountUpdateRequestDto accountUpdateRequestDto, @MappingTarget Account account);
}
