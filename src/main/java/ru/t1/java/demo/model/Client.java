package ru.t1.java.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Класс представляет сущность "Клиента".
 */
@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class Client extends AbstractPersistable<Long> {

    /**
     * Уникальный идентификатор клиента.
     */
    @NotNull
    @Column(name = "client_id")
    private UUID clientId;

    /**
     * Имя клиента.
     */
    @Column(name = "first_name")
    private String firstName;

    /**
     * Фамилия клиента.
     */
    @Column(name = "last_name")
    private String lastName;

    /**
     * Отчество клиента.
     */
    @Column(name = "middle_name")
    private String middleName;

    /**
     * Счета клиента.
     */
    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    private Set<Account> accounts = new HashSet<>();

    protected void setIdForMapping(Long id) {
        this.setId(id);
    }
}