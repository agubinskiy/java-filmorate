package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.CreateValidation;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class NewFilmRequest {
    @NotBlank(message = "Название фильма не может быть пустым", groups = CreateValidation.class)
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов",
            groups = CreateValidation.class)
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Длительность фильма должна быть положительной", groups = CreateValidation.class)
    private Integer duration;

    // @NotEmpty(message = "Должен быть указан хотя бы один жанр", groups = CreateValidation.class)
    private Set<GenreDtoForFilm> genres;

    private RateDtoForFilm mpa;

    private List<DirectorDtoForFilm> directors;
}
