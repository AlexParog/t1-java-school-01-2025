package ru.t1.java.demo.service;

import ru.t1.java.demo.model.DataSourceErrorLog;

/**
 * Интерфейс сервисного слоя для управления сущностью {@link DataSourceErrorLog}.
 * <p>
 * Предоставляет метод для сохранения логов ошибок, возникающих при работе с источниками данных.
 */
public interface DataSourceErrorLogService {
    /**
     * Сохраняет лог ошибки источника данных.
     *
     * @param dataSourceErrorLog Объект лога ошибки.
     */
    void saveLog(DataSourceErrorLog dataSourceErrorLog);
}
