package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Map;

public interface FilmStorage {
    Optional<Film> getFilm(Long id);

    Collection<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getMostLikedFilms(int count);

    Film addLike(Long filmId, Long userId);

    void deleteFilm(Long filmId);

    List<Film> getMostLikedFilmsByGenreYear(int count, long genreId, int year);

    List<Film> getMostLikedFilmsByGenre(int count, long genreId);

    List<Film> getMostLikedFilmsByYear(int count, int year);

    Map<Long, Map<Long, Double>> getAllLikes();

    List<Film> getCommonFilms(Long userId, Long friendId);

    void removeLike(Long userId, Long filmId);
}
