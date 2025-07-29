package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.CreateValidation;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @Email(message = "Некорректный формат почты", groups = CreateValidation.class)
    @NotBlank(message = "Почта не может быть пустой", groups = CreateValidation.class)
    private String email;

    @NotBlank(message = "Логин не может быть пустым", groups = CreateValidation.class)
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов", groups = CreateValidation.class)
    private String login;

    private String name;

    @PastOrPresent(message = "Некорректная дата рождения", groups = CreateValidation.class)
    private LocalDate birthday;
}
