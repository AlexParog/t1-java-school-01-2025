package ru.t1.java.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.t1.java.demo.dto.api.TransactionRequestDto;
import ru.t1.java.demo.dto.api.TransactionResponseDto;
import ru.t1.java.demo.service.TransactionService;

/**
 * Контроллер REST API для сущности {@link ru.t1.java.demo.model.Transaction}.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    /**
     * Сервис для работы с транзакциями.
     */
    private final TransactionService transactionService;

    /**
     * Создаёт новую транзакцию.
     *
     * @param transactionRequestDto DTO с данными для создания транзакции.
     * @return ответ с созданной транзакцией и статусом HTTP 201 Created.
     */
    @Operation(summary = "Создание транзакции", description = "Создает новую транзакцию и сохраняет в БД")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Транзакция успешно создана",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDto createTransaction(
            @Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        return transactionService.createTransaction(transactionRequestDto);
    }

    /**
     * Получает транзакцию по его идентификатору.
     *
     * @param id идентификатор транзакции.
     * @return ответ с найденной транзакцией и статусом HTTP 200 OK.
     */
    @Operation(summary = "Получение транзакции по ID", description = "Возвращает информацию о транзакции по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция найдена",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Транзакция не найдена",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public TransactionResponseDto getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id);
    }

    //TODO: getTransactionsByAccountId

    /**
     * Обновляет данные транзакции по его идентификатору.
     *
     * @param id                    идентификатор транзакции.
     * @param transactionRequestDto DTO с обновлёнными данными транзакции.
     * @return обновлённая транзакция и статус HTTP 200 OK.
     */
    @Operation(summary = "Обновление информации транзакции по ID",
            description = "Возвращает транзакцию по ID с обновленными полями")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обновленная транзакция",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ошибка при обновлении транзакции",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public TransactionResponseDto updateTransactionById(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        return transactionService.updateTransactionById(id, transactionRequestDto);
    }

    /**
     * Архивирует транзакцию по его идентификатору.
     * Устанавливает дату архивации для транзакции.
     *
     * @param id идентификатор транзакции.
     * @return архивированная транзакция и статус HTTP 200 OK.
     */
    @Operation(summary = "Архивирует транзакцию по ID", description = "Возвращает транзакцию по ID с датой архивации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Архивированная транзакция",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Ошибка при архивации транзакции",
                    content = @Content)
    })
    @DeleteMapping("/archive/{id}")
    public TransactionResponseDto archiveTransactionById(@PathVariable Long id) {
        return transactionService.archiveTransactionById(id);
    }
}
