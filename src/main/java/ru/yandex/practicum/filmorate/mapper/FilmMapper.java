package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDtoForFilm;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rate;

import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        film.setGenres(Optional.ofNullable(request.getGenres())
                .orElseGet(Collections::emptySet)
                .stream()
                .sorted(Comparator.comparing(GenreDtoForFilm::getId))
                .map(dto -> Genre.fromId(dto.getId()))
                .toList()
        );

        film.setMpa(Rate.fromId(request.getMpa().getId()));

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        dto.setGenres(film.getGenres().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .map(GenreMapper::mapToGenreDto)
                .toList()
        );

        dto.setMpa(RateMapper.mapToRateDto(film.getMpa()));
        dto.setLikes(film.getLikes());

        return dto;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) {
            film.setName(request.getName());
        }
        if (request.hasDescription()) {
            film.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            film.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            film.setDuration(request.getDuration());
        }
        if (request.hasGenres()) {
            film.setGenres(request.getGenres().stream()
                    .map(genre -> Genre.fromId(genre.getId()))
                    .toList());
        }
        if (request.hasRate()) {
            film.setMpa(Rate.fromId(request.getMpa().getId()));
        }
        return film;
    }
}
