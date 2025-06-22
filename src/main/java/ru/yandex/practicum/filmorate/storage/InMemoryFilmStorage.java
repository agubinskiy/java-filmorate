package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long counter;
    private static final Comparator<Film> filmLikesComparator = Comparator.comparingInt((Film film) ->
            film.getLikes().size()).reversed();

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
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        log.info("Фильм успешно обновлён {}", film);
        return film;
    }

    public Optional<Film> getFilm(Long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    public List<Film> getMostLikedFilms(int count) {
        return findAllFilms().stream()
                .sorted(filmLikesComparator)
                .limit(count)
                .toList();
    }
}
