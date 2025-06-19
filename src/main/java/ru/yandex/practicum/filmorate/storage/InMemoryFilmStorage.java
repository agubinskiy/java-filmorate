package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long counter;

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка при добавлении фильма. Дата релиза не может быть до 28.12.1895. Передано: {}",
                    film.getReleaseDate());
            throw new ValidationException("releaseDate", "Дата релиза не может быть до 28.12.1895");
        }
        film.setId(++counter);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (newFilm.getId() == null) {
            log.warn("Ошибка при обновлении фильма. Не передан Id фильма");
            throw new ValidationException("Id", "Id фильма не может быть пустым");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
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
            log.info("Фильм успешно обновлён {}", oldFilm);
            return oldFilm;
        }
        log.warn("Ошибка при обновлении фильма. Не найдено фильма с идентификатором {}",
                newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public void deleteFilm(Long id) {
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Фильм с id={} успешно удалён", id);
        } else {
            log.info("Фильм с id={} не найден", id);
        }
    }

    public Optional<Film> getFilm(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }
}
