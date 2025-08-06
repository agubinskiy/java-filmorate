package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.comparator.FilmComparatorDate;
import ru.yandex.practicum.filmorate.comparator.FilmComparatorLikes;

import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;

import ru.yandex.practicum.filmorate.model.Director;

import ru.yandex.practicum.filmorate.model.Event;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToFilm;
import static ru.yandex.practicum.filmorate.mapper.FilmMapper.mapToFilmDto;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final DirectorStorage directorStorage;

    private final FilmComparatorLikes filmComparatorLikes = new FilmComparatorLikes();
    private final FilmComparatorDate filmComparatorDate = new FilmComparatorDate();

    private final EventStorage eventStorage;

    @Autowired
    public FilmService(DirectorStorage directorStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       EventStorage eventStorage) {
        this.directorStorage = directorStorage;
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
        // Обработка режиссеров
        // Получаем режиссеров из БД по id и устанавливаем их в фильм
        if (request.getDirectors() != null && !request.getDirectors().isEmpty()) {
            getDirectorsFilm(film, request);
        }

        log.debug("Добавление фильма успешно {}", film);
        return mapToFilmDto(film);
    }


    private void getDirectorsFilm(Film film, NewFilmRequest request) {
        List<Director> directorsList = directorStorage.findAll();
        // Создаем пустую карту для хранения жанров по id
        Map<Long, Director> directorMap = new HashMap<>();
        // Проходим по всем жанрам из списка directorsDtoList
        for (Director d : directorsList) {
            // Получаем id режиссёра
            Long id = d.getId();
            // Помещаем жанр в карту с ключом - его id
            directorMap.put(id, d);
        }
        List<Director> directors = new ArrayList<>();
        for (DirectorDtoForFilm directorRequest : request.getDirectors()) {
            Director director = directorMap.get(directorRequest.getId());
            if (director != null) {
                directors.add(director);
            } else {
                throw new NotFoundException("Режиссёр с данным id не найден");
            }
        }
        film.setDirectors(directors);
        filmStorage.saveFilmDirectors(film.getId(), directors);
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
        if (request.getDirectors() != null) {
            updateDirectorsFilm(updatedFilm, request);
        }
        log.debug("Обновление успешно {}", updatedFilm);
        return mapToFilmDto(updatedFilm);
    }

    public void updateDirectorsFilm(Film updatedFilm, UpdateFilmRequest request) {
        List<Director> directors = new ArrayList<>();
        for (DirectorDtoForFilm d : request.getDirectors()) {
            Director director = directorStorage.findById(d.getId()).orElseThrow();
            directors.add(director);
        }
        // Обновляем связи режиссеров
        filmStorage.updateFilmDirectors(request.getId(), directors);
        // Обновляем список режиссеров в объекте фильма
        updatedFilm.setDirectors(directors);

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
                .timestamp(Timestamp.from(Instant.now()))
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
        filmStorage.removeLike(userId, filmId);
        log.info("Лайк успешно удалён.");
        //добавить событие в ленту
        eventStorage.addEvent(Event.builder()
                .userId(userId)
                .eventType(EventType.LIKE)
                .operation(OperationType.REMOVE)
                .timestamp(Timestamp.from(Instant.now()))
                .entityId(filmId)
                .build());
        return mapToFilmDto(filmStorage.getFilm(filmId).get());
    }

    public List<FilmDto> getMostLikedFilms(int count) {
        return filmStorage.getMostLikedFilms(count).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public List<FilmDto> searchFilms(String query, SearchBy by) {
        return filmStorage.searchFilms(query, by).stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public List<FilmDto> getDirectorFilms(long directorId) {
        if (directorStorage.findById(directorId).isEmpty()) {
            log.warn("Ошибка при поиске фильмов. Режиссёр с id={} не найден", directorId);
            throw new NotFoundException("Режиссёр с id=" + directorId + " не найден");
        }
        List<Film> films = filmStorage.getFilmsByIdDirector(directorId);
        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getFilmsDirectorSortByLikes(long directorId) {
        if (directorStorage.findById(directorId).isEmpty()) {
            log.warn("Ошибка при поиске фильмов. Режиссёр с id={} не найден", directorId);
            throw new NotFoundException("Режиссёр с id=" + directorId + " не найден");
        }
        return filmStorage.getFilmsByIdDirector(directorId).stream()
                .sorted(filmComparatorLikes)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public List<FilmDto> getFilmsDirectorSortByYear(long directorId) {
        if (directorStorage.findById(directorId).isEmpty()) {
            log.warn("Ошибка при поиске фильмов. Режиссёр с id={} не найден", directorId);
            throw new NotFoundException("Режиссёр с id=" + directorId + " не найден");
        }
        return filmStorage.getFilmsByIdDirector(directorId).stream()
                .sorted(filmComparatorDate)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
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

    public List<FilmDto> getMostLikedFilmsByGenreYear(int count, long genreId, int year) {
        if (genreId > 0 && year > 0) {
            return filmStorage.getMostLikedFilmsByGenreYear(count, genreId, year).stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
        } else if (genreId > 0) {
            return filmStorage.getMostLikedFilmsByGenre(count, genreId).stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
        } else if (year > 0) {
            return filmStorage.getMostLikedFilmsByYear(count, year).stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
        } else {
            return filmStorage.getMostLikedFilms(count).stream()
                    .map(FilmMapper::mapToFilmDto)
                    .toList();
        }
    }

    public void deleteFilm(Long filmId) {
        if (filmStorage.getFilm(filmId).isEmpty()) {
            log.warn("Ошибка при удалении фильма. Фильм с id={} не найден", filmId);
            throw new NotFoundException("Фильм с id=" + filmId + " не найден");
        }
        filmStorage.deleteFilm(filmId);
        log.info("Фильм с id={} успешно удален", filmId);
    }

    public List<FilmDto> getCommonFilms(long userId, long friendId) {
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при запросе общих фильмов. Пользователь с userId {} не найден", userId);
        }
        if (userStorage.getUser(userId).isEmpty()) {
            log.warn("Ошибка при запросе общих фильмов. Пользователь с friendId {} не найден", userId);
        }
        return filmStorage.getCommonFilms(userId, friendId)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .sorted(Comparator.comparingInt((FilmDto film)  -> film.getLikes().size()).reversed())
                .toList();
    }
}
