package ru.t1.java.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.t1.java.demo.model.DataSourceErrorLog;

/**
 * JPA Репозиторий для работы с сущностью {@link DataSourceErrorLog}
 */
@Repository
public interface DataSourceErrorLogRepository extends JpaRepository<DataSourceErrorLog, Long> {
}
