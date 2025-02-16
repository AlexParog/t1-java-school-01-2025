package ru.t1.java.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.api.AccountCreateRequestDto;
import ru.t1.java.demo.dto.api.AccountResponseDto;
import ru.t1.java.demo.dto.api.AccountUpdateRequestDto;
import ru.t1.java.demo.service.AccountService;

/**
 * Контроллер REST API для сущности {@link ru.t1.java.demo.model.Account}.
 */
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    /**
     * Сервис для работы со счетами.
     */
    private final AccountService accountService;

    /**
     * Создаёт новый счет.
     *
     * @param accountCreateRequestDto DTO с данными для создания счета.
     * @return ответ с созданным счетом и статусом HTTP 201 Created.
     */
    @Operation(summary = "Создание счета", description = "Создает новый счет и сохраняет в БД")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Счет успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountCreateRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDto createAccount(
            @RequestBody AccountCreateRequestDto accountCreateRequestDto) {
        return accountService.createAccount(accountCreateRequestDto);
    }

    /**
     * Получает счет по его идентификатору.
     *
     * @param id идентификатор счета.
     * @return ответ с найденным счетом и статусом HTTP 200 OK.
     */
    @Operation(summary = "Получение счета по ID", description = "Возвращает информацию о счете по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Счет найден",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Счет не найден",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public AccountResponseDto getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    /**
     * Обновляет данные счета по его идентификатору.
     *
     * @param id                      идентификатор счета.
     * @param accountUpdateRequestDto DTO с обновлёнными данными счета.
     * @return обновлённый счет и статус HTTP 200 OK.
     */
    @Operation(summary = "Обновление информации о счете по ID", description = "Возвращает счет по ID с обновленными полями")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обновленный счет",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountUpdateRequestDto.class))),
            @ApiResponse(responseCode = "404", description = "Ошибка при обновлении счета",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public AccountResponseDto updateAccountById(
            @PathVariable Long id,
            @RequestBody AccountUpdateRequestDto accountUpdateRequestDto) {
        return accountService.updateAccountById(id, accountUpdateRequestDto);
    }

    /**
     * Архивирует счет по его идентификатору.
     * Устанавливает дату архивации для счета.
     *
     * @param id идентификатор счета.
     * @return архивированный счет и статус HTTP 200 OK.
     */
    @Operation(summary = "Архивирует счет по ID", description = "Возвращает счет по ID с датой архивации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Архивированный счет",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ошибка при архивации счета",
                    content = @Content)
    })
    @DeleteMapping("/archive/{id}")
    public AccountResponseDto archiveAccountById(@PathVariable Long id) {
        return accountService.archiveAccountById(id);
    }

}
