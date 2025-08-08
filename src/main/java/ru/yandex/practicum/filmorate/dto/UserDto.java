package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @Email(message = "Некорректный формат почты")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов")
    @Size(max = 40, message = "Логин пользователя не может быть длиннее 40 символов")
    private String login;

    @Size(max = 40, message = "Логин пользователя не может быть длиннее 40 символов")
    private String name;

    @PastOrPresent(message = "Некорректная дата рождения")
    private LocalDate birthday;

    private Set<Long> friends;
}
