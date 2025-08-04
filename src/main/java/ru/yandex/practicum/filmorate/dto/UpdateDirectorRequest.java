package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.UpdateValidation;

@Data
public class UpdateDirectorRequest {

    @NotNull(message = "Id не может быть пустым", groups = UpdateValidation.class)
    private Long id;

    private String name;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }
}
