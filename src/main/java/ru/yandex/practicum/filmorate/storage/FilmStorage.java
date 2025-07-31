package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {
    Optional<Film> getFilm(Long id);

    Collection<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getMostLikedFilms(int count);

    Film addLike(Long filmId, Long userId);

    Map<Long, Map<Long, Double>> getAllLikes();

    List<Long> getCommonFilms(Long userId, Long friendId);
}
