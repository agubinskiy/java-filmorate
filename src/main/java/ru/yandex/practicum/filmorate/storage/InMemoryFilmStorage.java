package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    @Getter
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

    public List<Film> getMostLikedFilms(int count) {
        return findAllFilms().stream()
                .sorted(filmLikesComparator)
                .limit(count)
                .toList();
    }

    @Override
    public Film addLike(Long filmId, Long userId) {
        Film film = getFilm(filmId).orElseThrow();
        film.getLikes().add(userId);
        return film;
    }

    @Override
    public List<Film> getMostLikedFilmsByGenreYear(int count, long genreId, int year) {
        return null;
    }

    @Override
    public List<Film> getMostLikedFilmsByGenre(int count, long genreId) {
        return null;
    }

    @Override
    public List<Film> getMostLikedFilmsByYear(int count, int year) {
        return null;
    }

    @Override
    public void saveFilmDirectors(long filmId, List<Director> directors){
    }

    @Override
    public List<Film> getFilmsByIdDirector(long directorId) {
        return new ArrayList<>();
    }

    @Override
    public void updateFilmDirectors(Long filmId, List<Director> directors) {
    }
}
