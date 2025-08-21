package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.UpdateValidation;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    @NotNull(message = "Id не может быть пустым", groups = UpdateValidation.class)
    private Long id;

    @Email(message = "Некорректный формат почты", groups = UpdateValidation.class)
    private String email;

    @Pattern(regexp = "^\\S+$", message = "Логин не должен содержать пробелов", groups = UpdateValidation.class)
    @Size(max = 40, message = "Логин пользователя не может быть длиннее 40 символов", groups = UpdateValidation.class)
    private String login;

    @Size(max = 40, message = "Имя пользователя не может быть длиннее 40 символов", groups = UpdateValidation.class)
    private String name;

    @PastOrPresent(message = "Некорректная дата рождения", groups = UpdateValidation.class)
    private LocalDate birthday;

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return !(login == null || login.isBlank());
    }

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return !(birthday == null);
    }
}
