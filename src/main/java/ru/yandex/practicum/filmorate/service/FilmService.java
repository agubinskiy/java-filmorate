package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToFilm;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToFilmDto;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Autowired
    public FilmService(@Qualifier("filmDboStorage") FilmStorage filmStorage,
                       @Qualifier("userDboStorage") UserStorage userStorage,
                       EventStorage eventStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.eventStorage = eventStorage;
    }

    public Collection<FilmDto> findAllFilms() {
        return filmStorage.findAllFilms().stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public FilmDto addFilm(@Valid NewFilmRequest request) {
        log.debug("Начинается добавление фильма по запросу {}", request);
        if (request.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Ошибка при добавлении фильма. Дата релиза не может быть до 28.12.1895. Передано: {}",
                    request.getReleaseDate());
            throw new ValidationException("releaseDate", "Дата релиза не может быть до 28.12.1895");
        }
        if (request.getMpa() != null) {
            validateRate(request.getMpa());
        }
        if (request.getGenres() != null) {
            validateGenre(request.getGenres());
        }
        Film film = mapToFilm(request);
        log.debug("Запрос на добавление фильма конвертирован в объект класса Film {}", film);
        film = filmStorage.addFilm(film);
        log.debug("Добавление фильма успешно {}", film);
        return mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        log.debug("Начинается обновление фильма по запросу {}", request);
        if (request.getMpa() != null) {
            validateRate(request.getMpa());
        }
        if (request.getGenres() != null) {
            validateGenre(request.getGenres());
        }
        Film updatedFilm = filmStorage.getFilm(request.getId())
                .map(film -> FilmMapper.updateFilmFields(film, request))
                .orElseThrow(() -> {
                    log.warn("Ошибка при обновлении фильма. Не найдено фильма с идентификатором {}",
                            request.getId());
                    return new NotFoundException("Фильм с id = " + request.getId() + " не найден");
                });
        log.debug("Запрос на обновление фильма конвертирован в объект класса Film {}", updatedFilm);
        updatedFilm = filmStorage.updateFilm(updatedFilm);
        log.debug("Обновление успешно {}", updatedFilm);
        return mapToFilmDto(updatedFilm);
    }

    public FilmDto getFilm(Long filmId) {
        return filmStorage.getFilm(filmId)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> {
                    log.warn("Ошибка при поиске фильма. Фильм с id={} не найден", filmId);
                    return new NotFoundException("Фильм с id=" + filmId + " не найден");
                });
    }

    public FilmDto addLike(Long filmId, Long userId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при добавлении лайка. Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        if (filmStorage.getFilm(filmId).isEmpty()) {
            log.warn("Ошибка при добавлении лайка. Фильм с id={} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        filmStorage.addLike(filmId, userId);
        log.info("Лайк успешно добавлен.");
        //добавить событие в ленту
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(OperationType.ADD)
                .timestamp(Instant.now().getEpochSecond())
                .entityId(filmId)
                .build());
        return mapToFilmDto(filmStorage.getFilm(filmId).get());
    }

    public FilmDto deleteLike(Long filmId, Long userId) {
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
        //добавить событие в ленту
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(OperationType.REMOVE)
                .timestamp(Instant.now().getEpochSecond())
                .entityId(filmId)
                .build());
        return mapToFilmDto(filmStorage.getFilm(filmId).get());
    }

    public List<FilmDto> getMostLikedFilms(int count) {
        return filmStorage.getMostLikedFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    private void validateRate(RateDtoForFilm mpa) {
        if (mpa.getId() < 1 || mpa.getId() > 5) {
            log.debug("Валидация рейтинга не пройдена. Рейтинг с идентификатором id={} не найден", mpa.getId());
            throw new NotFoundException("Рейтинг с идентификатором id=" + mpa.getId() + " не существует");
        }
    }

    private void validateGenre(Set<GenreDtoForFilm> genres) {
        genres.forEach(genre -> {
            if (genre.getId() < 1 || genre.getId() > 6) {
                log.debug("Валидация жанра не пройдена. Жанр с идентификатором id={} не найден", genre.getId());
                throw new NotFoundException("Жанр с идентификатором id=" + genre.getId() + " не существует");
            }
        });
    }
}
