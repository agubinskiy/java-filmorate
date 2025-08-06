package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.CreateValidation;

@Data
public class NewDirectorRequest {

    @NotBlank(message = "Имя режиссёра не может быть пустым", groups = CreateValidation.class)
    private String name;
}
