package ru.t1.java.demo.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.DataSourceErrorLog;
import ru.t1.java.demo.repository.DataSourceErrorLogRepository;
import ru.t1.java.demo.service.DataSourceErrorLogService;

/**
 * Реализация сервисного слоя для управления сущностью {@link DataSourceErrorLog}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceErrorLogServiceImpl implements DataSourceErrorLogService {

    private final DataSourceErrorLogRepository dataSourceErrorLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveLog(DataSourceErrorLog dataSourceErrorLog) {
        dataSourceErrorLogRepository.save(dataSourceErrorLog);
    }
}
