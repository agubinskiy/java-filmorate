package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Comparator<Film> filmLikesComparator = Comparator.comparingInt((Film film) ->
            film.getLikes().size()).reversed();

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteFilm(id);
    }

    public Film getFilm(Long filmId) {
        if (filmStorage.getFilm(filmId).isEmpty()) {
            log.warn("Ошибка при поиске фильма. Фильм с id={} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        return filmStorage.getFilm(filmId).get();
    }

    public Film addLike(Long filmId, Long userId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при добавлении лайка. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (filmStorage.getFilm(filmId).isEmpty()) {
            log.warn("Ошибка при добавлении лайка. Фильм с id={} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        filmStorage.getFilm(filmId).get().getLikes().add(userId);
        log.info("Лайк успешно добавлен.");
        return filmStorage.getFilm(filmId).get();
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при удалении лайка. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (filmStorage.getFilm(filmId).isEmpty()) {
            log.warn("Ошибка при удалении лайка. Фильм с id={} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        filmStorage.getFilm(filmId).get().getLikes().remove(userId);
        log.info("Лайк успешно удалён.");
        return filmStorage.getFilm(filmId).get();
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted(filmLikesComparator)
                .limit(count)
                .toList();
    }
}
