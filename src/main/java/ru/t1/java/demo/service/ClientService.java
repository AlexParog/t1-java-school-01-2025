package ru.t1.java.demo.service;

import ru.t1.java.demo.model.Client;

import java.io.IOException;
import java.util.List;

/**
 * Интерфейс сервисного слоя для управления Клиентами.
 * <p>
 * Предоставляет методы для работы с данными клиентов, например, парсинг из JSON.
 */
public interface ClientService {
    /**
     * Парсит список клиентов из JSON.
     *
     * @return Список клиентов, полученных из JSON.
     * @throws IOException Если произошла ошибка чтения JSON.
     */
    List<Client> parseJson() throws IOException;
}
