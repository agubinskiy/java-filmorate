package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

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
        Film oldFilm = filmStorage.getFilm(newFilm.getId()).orElseThrow(() -> {
            log.warn("Ошибка при обновлении фильма. Не найдено фильма с идентификатором {}",
                    newFilm.getId());
            return new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        });
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.warn("Ошибка при обновлении фильма. Дата релиза не может быть до 28.12.1895. Передано: {}",
                        newFilm.getReleaseDate());
                throw new ValidationException("releaseDate", "Дата релиза не может быть до 28.12.1895");
            }
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        return filmStorage.updateFilm(oldFilm);
    }

    public Film getFilm(Long filmId) {
        return filmStorage.getFilm(filmId).orElseThrow(() -> {
            log.warn("Ошибка при поиске фильма. Фильм с id={} не найден", filmId);
            return new NotFoundException("Фильм с id=" + filmId + " не найден");
        });
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
        return filmStorage.getMostLikedFilms(count);
    }
}
