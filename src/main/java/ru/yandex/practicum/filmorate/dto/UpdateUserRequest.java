package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
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
    private String login;

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
