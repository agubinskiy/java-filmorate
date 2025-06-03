package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
@Getter
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка при добавлении фильма. Передано пустое название");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка при добавлении фильма. Описание слишком длинное, более 200 символов. Передано: {}",
                    film.getDescription().length());
            throw new ValidationException("Описание фильма не может быть больше 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка при добавлении фильма. Дата релиза не может быть до 28.12.1895. Передано: {}",
                    film.getReleaseDate());
            throw new ValidationException("Дата релиза некорректна");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка при добавлении фильма. Длительность фильма должна быть положительной. Передано: {}",
                    film.getDuration());
            throw new ValidationException("Продолжительность фильма некорректна");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка при обновлении фильма. Не передан Id фильма");
            throw new ValidationException("Id фильма не может быть пустым");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null) {
                if (newFilm.getName().isBlank()) {
                    log.warn("Ошибка при обновлении фильма. Передано пустое название");
                    throw new ValidationException("Название фильма не может быть пустым");
                }
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null) {
                if (newFilm.getDescription().length() > 200) {
                    log.warn("Ошибка при обновлении фильма. Описание слишком длинное, более 200 символов. Передано: {}",
                            newFilm.getDescription().length());
                    throw new ValidationException("Описание фильма не может быть больше 200 символов");
                }
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null) {
                if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                    log.warn("Ошибка при обновлении фильма. Дата релиза не может быть до 28.12.1895. Передано: {}",
                            newFilm.getReleaseDate());
                    throw new ValidationException("Дата релиза некорректна");
                }
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() != null) {
                if (newFilm.getDuration() <= 0) {
                    log.warn("Ошибка при обновлении фильма. Длительность фильма должна быть положительной. Передано: {}",
                            newFilm.getDuration());
                    throw new ValidationException("Продолжительность фильма некорректна");
                }
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Фильм успешно обновлён {}", oldFilm);
            return oldFilm;
        }
        log.warn("Ошибка при обновлении фильма. Не найдено фильма с идентификатором {}",
                newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
