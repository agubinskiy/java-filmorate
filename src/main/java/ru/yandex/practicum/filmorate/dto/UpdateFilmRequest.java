package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.UpdateValidation;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotNull(message = "Id не может быть пустым", groups = UpdateValidation.class)
    private Long id;

    private String name;

    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов",
            groups = UpdateValidation.class)
    private String description;

    private LocalDate releaseDate;

    @Positive(message = "Длительность фильма должна быть положительной", groups = UpdateValidation.class)
    private Integer duration;

    private Set<GenreDtoForFilm> genres;

    private RateDtoForFilm mpa;

    private List<DirectorDtoForFilm> directors;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasDuration() {
        return !(duration == null);
    }

    public boolean hasGenres() {
        return !(genres == null || genres.isEmpty());
    }

    public boolean hasRate() {
        return !(mpa == null);
    }

    public boolean hasDirectors() {
        return !(directors == null || directors.isEmpty());
    }
}
