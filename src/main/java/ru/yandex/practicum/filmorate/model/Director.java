package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Director {
    private Long id;
    @NotBlank(message = "Имя режиссёра не может быть пустым", groups = CreateValidation.class)
    private String name;

    public Director(Long directorId, String directorName) {
        this.id = directorId;
        this.name = directorName;
    }

    public Director() {
    }
}
