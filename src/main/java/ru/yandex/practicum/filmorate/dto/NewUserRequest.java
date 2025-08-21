package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @Email(message = "Некорректный формат почты", groups = CreateValidation.class)
    @NotBlank(message = "Почта не может быть пустой", groups = CreateValidation.class)
    private String email;

    @NotBlank(message = "Логин не может быть пустым", groups = CreateValidation.class)
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов", groups = CreateValidation.class)
    @Size(max = 40, message = "Логин пользователя не может быть длиннее 40 символов", groups = CreateValidation.class)
    private String login;

    @Size(max = 40, message = "Имя пользователя не может быть длиннее 40 символов", groups = CreateValidation.class)
    private String name;

    @PastOrPresent(message = "Некорректная дата рождения", groups = CreateValidation.class)
    private LocalDate birthday;
}
