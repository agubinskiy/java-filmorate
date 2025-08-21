package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateDirectorRequest {

    @NotNull(message = "Id не может быть пустым")
    private Long id;

    @Size(max = 100, message = "Имя режиссёра не может превышать 100 символов")
    private String name;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}
