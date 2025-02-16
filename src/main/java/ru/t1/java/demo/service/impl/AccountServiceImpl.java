package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.aop.LogDataSourceError;
import ru.t1.java.demo.aop.Metric;
import ru.t1.java.demo.dto.api.AccountCreateRequestDto;
import ru.t1.java.demo.dto.api.AccountResponseDto;
import ru.t1.java.demo.dto.api.AccountUpdateRequestDto;
import ru.t1.java.demo.dto.kafka.AccountKafkaDto;
import ru.t1.java.demo.exception.NotFoundException;
import ru.t1.java.demo.kafka.producer.AccountKafkaProducer;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Client;
import ru.t1.java.demo.model.enums.AccountTypeEnum;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.ClientRepository;
import ru.t1.java.demo.service.AccountService;
import ru.t1.java.demo.util.AccountMapper;

import java.time.LocalDateTime;

/**
 * Реализация сервисного слоя для управления сущностью {@link Account}.
 * Предоставляет методы для создания, обновления, архивирования и получения счетов.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    /**
     * Репозиторий для работы с сущностью {@link Account}.
     */
    private final AccountRepository accountRepository;

    /**
     * Маппер для преобразования DTO в сущности и обратно.
     */
    private final AccountMapper accountMapper;

    /**
     * Репозиторий для работы с сущностью {@link Client}.
     */
    private final ClientRepository clientRepository;

    /**
     * Компонент для отправки сообщений Kafka, связанных с сущностью {@link Account}.
     */
    private final AccountKafkaProducer accountKafkaProducer;

    /**
     * Создаёт новый счёт на основе данных из DTO.
     *
     * @param accountCreateRequestDto DTO с данными для создания счёта.
     * @return DTO с данными созданного счёта.
     */
    @Transactional
    @LogDataSourceError
    @Metric(10)
    @Override
    public AccountResponseDto createAccount(AccountCreateRequestDto accountCreateRequestDto) {
        log.info("Создание счета: {}", accountCreateRequestDto);

        Client client = findClientForAccount(accountCreateRequestDto.getClientId());

        Account account = accountMapper.toAccountAfterCreate(accountCreateRequestDto);
        account.setClient(client);
        account.setArchiveDate(null);

        accountRepository.save(account);

        // Отправка в Kafka
        sendAccountToKafka(account);

        AccountResponseDto accountResponseDto = accountMapper.toAccountResponseDto(account);
        log.info("Счет успешно создан с ID: {}", accountResponseDto.getId());
        return accountResponseDto;
    }

    /**
     * Регистрирует счёт на основе данных, полученных из Kafka.
     *
     * @param accountKafkaDto DTO с данными о счёте из Kafka.
     */
    @Transactional
    @LogDataSourceError
    @Metric(10)
    @Override
    public void registerAccountFromKafka(AccountKafkaDto accountKafkaDto) {
        log.info("Обработка счёта из Kafka: {}", accountKafkaDto);

        // Создание нового счёта на основе данных из Kafka
        Account account = new Account();
        account.setClient(clientRepository.findById(accountKafkaDto.getClientId())
                .orElseThrow(() -> new NotFoundException("Клиент с ID={0} не найден", accountKafkaDto.getClientId())));
        account.setAccountTypeEnum(AccountTypeEnum.valueOf(accountKafkaDto.getAccountTypeEnum()));
        account.setBalance(accountKafkaDto.getBalance());
        account.setArchiveDate(accountKafkaDto.getArchiveDate());

        accountRepository.save(account);

        log.info("Счёт успешно обработан: {}", account);
    }

    /**
     * Получает счёт по его идентификатору.
     *
     * @param id идентификатор счёта.
     * @return DTO с данными найденного счёта.
     */
    @Transactional(readOnly = true)
    @LogDataSourceError
    @Override
    public AccountResponseDto getAccountById(Long id) {
        log.info("Получение счета с ID: {}", id);

        Account account = findAccountOrNotFound(id);
        log.debug("Счет найден: {}", account);

        return accountMapper.toAccountResponseDto(account);
    }

    /**
     * Обновляет данные счёта по его идентификатору.
     *
     * @param id                      идентификатор счёта.
     * @param accountUpdateRequestDto DTO с обновлёнными данными счёта.
     * @return DTO с данными обновлённого счёта.
     */
    @Transactional
    @LogDataSourceError
    @Override
    public AccountResponseDto updateAccountById(Long id, AccountUpdateRequestDto accountUpdateRequestDto) {
        log.info("Обновление счета с ID: {} данными: {}", id, accountUpdateRequestDto);

        Account account = findAccountOrNotFound(id);
        accountMapper.updateAccountFromDto(accountUpdateRequestDto, account);

        accountRepository.save(account);
        log.info("Счет с ID: {} успешно обновлен", id);

        return accountMapper.toAccountResponseDto(account);
    }

    /**
     * Архивирует счёт по его идентификатору.
     *
     * @param id идентификатор счёта.
     * @return DTO с данными архивированного счёта.
     */
    @Transactional
    @LogDataSourceError
    @Override
    public AccountResponseDto archiveAccountById(Long id) {
        log.info("Архивирование счета с ID: {}", id);

        Account account = findAccountOrNotFound(id);

        account.setArchiveDate(LocalDateTime.now());
        accountRepository.save(account);

        log.info("Счет с ID: {} успешно архивирован на дату: {}", id, account.getArchiveDate());

        return accountMapper.toAccountResponseDto(account);
    }

    /**
     * Находит счёт по его идентификатору или выбрасывает исключение, если счёт не найден.
     *
     * @param id идентификатор счёта.
     * @return найденный счёт.
     * @throws NotFoundException если счёт не найден.
     */
    private Account findAccountOrNotFound(Long id) {
        log.debug("Поиск счета с ID: {}", id);

        return accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Счет с ID: {} не найден", id);
                    return new NotFoundException("Счет c id={0} не найден", id);
                });
    }

    /**
     * Отправляет данные о счёте в Kafka.
     *
     * @param account счёт, данные которого нужно отправить.
     */
    private void sendAccountToKafka(Account account) {
        AccountKafkaDto accountKafkaDto = new AccountKafkaDto();
        accountKafkaDto.setClientId(account.getClient().getId());
        accountKafkaDto.setAccountTypeEnum(String.valueOf(account.getAccountTypeEnum()));
        accountKafkaDto.setBalance(account.getBalance());
        accountKafkaDto.setArchiveDate(account.getArchiveDate());

        accountKafkaProducer.send(accountKafkaDto);
    }

    /**
     * Находит клиента по его идентификатору или выбрасывает исключение, если клиент не найден.
     *
     * @param clientId идентификатор клиента.
     * @return найденный клиент.
     * @throws NotFoundException если клиент не найден.
     */
    private Client findClientForAccount(Long clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> {
                    log.error("Клиент с ID: {} не найден", clientId);
                    return new NotFoundException("Клиент c id={0} не найден", clientId);
                });
    }
}