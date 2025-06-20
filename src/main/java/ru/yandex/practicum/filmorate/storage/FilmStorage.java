package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> getFilm(Long id);

    Collection<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Map<Long, Film> getFilms();

    List<Film> getMostLikedFilms(int count);
}
