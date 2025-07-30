package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    Optional<Film> getFilm(Long id);

    Collection<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getMostLikedFilms(int count);

    Film addLike(Long filmId, Long userId);

    List<Long> getUserLikes(Long userId);

    List<Long> getCommonFilms(Long userId, Long friendId);
}
