package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class Film {
    @NotNull(message = "Id не может быть пустым", groups = UpdateValidation.class)
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым",
            groups = {CreateValidation.class, UpdateValidation.class})
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов",
            groups = {CreateValidation.class, UpdateValidation.class})
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Длительность фильма должна быть положительной",
            groups = {CreateValidation.class, UpdateValidation.class})
    private Integer duration;

    private final Set<Long> likes = new HashSet<>();
}
