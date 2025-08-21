package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class NewDirectorRequest {

    @NotBlank(message = "Имя режиссёра не может быть пустым")
    @Size(max = 100, message = "Имя режиссёра не может превышать 100 символов")
    private String name;
}
