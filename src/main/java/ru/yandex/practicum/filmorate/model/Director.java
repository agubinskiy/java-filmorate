package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Director {
    private Long id;
    @NotBlank(message = "Имя режиссёра не может быть пустым", groups = CreateValidation.class)
    @Size(max = 100, message = "Имя режиссёра не может превышать 100 символов")
    private String name;

    public Director(Long directorId, String directorName) {
        this.id = directorId;
        this.name = directorName;
    }

    public Director() {
    }
}
