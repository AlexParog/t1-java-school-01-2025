package ru.t1.java.demo.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.t1.java.demo.model.Account;

import java.util.HashSet;
import java.util.Set;

/**
 * DTO-запроса для создания и обновления {@link ru.t1.java.demo.model.Client}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientRequestDto {

    /**
     * Имя клиента.
     */
    @NotBlank
    @JsonProperty("first_name")
    private String firstName;

    /**
     * Фамилия клиента.
     */
    @NotBlank
    @JsonProperty("last_name")
    private String lastName;

    /**
     * Отчество клиента.
     */
    @Nullable
    @JsonProperty("middle_name")
    private String middleName;

    /**
     * Счета клиента.
     */
    @NotNull
    @JsonProperty("accounts")
    private Set<Account> accounts = new HashSet<>();
}
