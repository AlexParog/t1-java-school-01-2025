package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;

import java.util.Arrays;

/**
 * Реализация сервисного слоя для управления сущностью {@link DataSourceErrorLog}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {

    /**
     * Репозиторий для работы с сущностью {@link DataSourceErrorLog}.
     */
    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    /**
     * Сохраняет лог ошибки в базу данных.
     *
     * @param e               исключение, вызвавшее ошибку.
     * @param methodSignature сигнатура метода, в котором произошла ошибка.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveLog(Exception e, String methodSignature) {
        DataSourceErrorLog dataSourceErrorLog = new DataSourceErrorLog();
        dataSourceErrorLog.setMessage(e.getMessage());
        dataSourceErrorLog.setStacktrace(Arrays.toString(e.getStackTrace()));
        dataSourceErrorLog.setMethodSignature(methodSignature);

        dataSourceErrorLogRepository.save(dataSourceErrorLog);
    }
}
