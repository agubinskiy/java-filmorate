package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @NotNull(message = "Id не может быть пустым", groups = UpdateValidation.class)
    private Long id;

    @Email(message = "Некорректный формат почты",
            groups = {CreateValidation.class, UpdateValidation.class})
    @NotBlank(message = "Email не может быть пустым",
            groups = {CreateValidation.class, UpdateValidation.class})
    private String email;

    @NotBlank(message = "Логин не может быть пустым", groups = {CreateValidation.class, UpdateValidation.class})
    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов",
            groups = {CreateValidation.class, UpdateValidation.class})
    private String login;

    private String name;

    @PastOrPresent(message = "Некорректная дата рождения", groups = {CreateValidation.class, UpdateValidation.class})
    private LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();
}
