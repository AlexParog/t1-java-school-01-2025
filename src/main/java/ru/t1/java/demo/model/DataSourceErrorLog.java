package ru.t1.java.demo.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDateTime;

/**
 * Класс представляет сущность "Логирующего сообщения об исключениях".
 */
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "data_source_error_log")
public class DataSourceErrorLog extends AbstractPersistable<Long> {

    /**
     * Текст стектрейса исключения.
     */
    // @Lob
    // @Basic(fetch = FetchType.LAZY)
    @Column(name = "stacktrace", columnDefinition = "TEXT")
    private String stacktrace;

    /**
     * Сообщение.
     */
    @Column(name = "message")
    private String message;

    /**
     * Сигнатура метода.
     */
    @Column(name = "method_signature")
    private String methodSignature;

    /**
     * Дата архивирования товара.
     */
    @Nullable
    @Column(name = "archive_date")
    private LocalDateTime archiveDate;

    /**
     * Проверяет, является ли лог "удалённым".
     * Лог считается удалённым, если поле {@code archiveDate} не равно {@code null}.
     *
     * @return {@code true}, если лог удалён, иначе {@code false}
     */
    public boolean isDeleted() {
        return archiveDate != null;
    }
}
