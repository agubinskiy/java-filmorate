package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.GenreDtoForFilm;
import ru.yandex.practicum.filmorate.model.Genre;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenreMapper {

    public static GenreDto mapToGenreDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getDisplayName());
        return dto;
    }

    public static GenreDtoForFilm mapToGenreDtoForFilm(Genre genre) {
        GenreDtoForFilm dto = new GenreDtoForFilm();
        dto.setId(genre.getId());
        return dto;
    }
}
