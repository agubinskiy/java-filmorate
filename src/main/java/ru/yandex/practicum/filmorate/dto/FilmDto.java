package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Director;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов")
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Длительность фильма должна быть положительной")
    private Integer duration;

    private RateDto mpa;

    @NotEmpty(message = "Должен быть указан хотя бы один жанр")
    private List<GenreDto> genres;

    private Set<Long> likes;

    @NotEmpty(message = "Должен быть указан хотя бы один режиссёр")
    private List<Director> directors;
}
