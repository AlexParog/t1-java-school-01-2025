package ru.t1.java.demo.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.t1.java.demo.dto.ClientDto;
import ru.t1.java.demo.dto.ClientRequestDto;
import ru.t1.java.demo.dto.ClientResponseDto;
import ru.t1.java.demo.model.Client;

/**
 * Маппер для преобразования между сущностью {@link Client} и DTO объектами.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {AccountMapper.class})
public interface ClientMapper {

    /**
     * Преобразует сущность {@link Client} в объект {@link ClientResponseDto}.
     *
     * @param client объект клиента.
     * @return DTO клиента.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "middleName", source = "middleName")
    @Mapping(target = "accounts", source = "accounts")
    ClientResponseDto toClientResponseDto(Client client);

    /**
     * Преобразует сущность {@link ClientRequestDto} в объект {@link Client}.
     *
     * @param clientRequestDto DTO с данными для создания клиента.
     * @return объект сущности клиента.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "middleName", source = "middleName")
    @Mapping(target = "accounts", source = "accounts")
    Client toClient(ClientRequestDto clientRequestDto);

    /**
     * Преобразует сущность {@link Client} в объект {@link ClientDto}.
     * Чтобы не было конфликтов, пока оставил в слоях сущность ClientDto (потом проведу рефакторинг).
     *
     * @param client объект клиента
     * @return ClientDto клиента.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "middleName", source = "middleName")
    ClientDto toClientDto(Client client);

    /**
     * Преобразует сущность {@link ClientDto} в объект {@link Client}.
     * Чтобы не было конфликтов, пока оставил в слоях сущность ClientDto (потом проведу рефакторинг).
     *
     * @param clientDto DTO с данными клиента.
     * @return объект сущности клиента.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "middleName", source = "middleName")
    Client toClient(ClientDto clientDto);
}
